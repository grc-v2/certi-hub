package com.certimaster.identity.application.command;

import com.certimaster.shared.constant.Status;
import com.certimaster.shared.security.JwtProperties;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.UnauthorizedException;
import com.certimaster.shared.security.JwtProvider;
import com.certimaster.identity.domain.service.EmailVerificationService;
import com.certimaster.identity.domain.service.OAuthStateService;
import com.certimaster.identity.application.query.PermissionService;
import com.certimaster.identity.domain.service.RefreshTokenService;
import com.certimaster.identity.domain.service.TokenBlacklistService;
import com.certimaster.identity.domain.service.OAuthProviderConfig;
import com.certimaster.identity.adapter.web.response.LoginResponse;
import com.certimaster.identity.adapter.web.response.RegisterResponse;
import com.certimaster.identity.adapter.web.response.EmailResult;
import com.certimaster.identity.adapter.web.response.OAuthAuthorizeResult;
import com.certimaster.identity.adapter.web.response.OAuthCallbackResult;
import com.certimaster.identity.adapter.web.response.OAuthUserProfile;
import com.certimaster.identity.domain.entity.RefreshToken;
import com.certimaster.identity.domain.entity.Role;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.adapter.persistence.RoleRepository;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.identity.domain.event.EmailVerificationRequestedEvent;
import com.certimaster.identity.domain.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthCommandService {

    private static final Logger log = LoggerFactory.getLogger(AuthCommandService.class);
    private static final String PROVIDER_GOOGLE = "google";
    private static final String PROVIDER_GITHUB = "github";
    private static final String OAUTH_CALLBACK_PATH = "/oauth/callback";

    private final JwtProperties jwtProperties;
    private final UserWriteRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenService refreshTokenService;
    private final PermissionService permissionService;
    private final TokenBlacklistService tokenBlacklistService;
    private final OAuthStateService oauthStateService;
    private final ApplicationEventPublisher applicationEvents;
    private final RestTemplate restTemplate;

    @Value("${auth.oauth2.redirect-allowlist:http://localhost:3000,https://certimaster.com}")
    private String oauthRedirectAllowlist;

    @Value("${auth.oauth2.google.client-id:}")
    private String googleClientId;

    @Value("${auth.oauth2.google.client-secret:}")
    private String googleClientSecret;

    @Value("${auth.oauth2.google.authorize-url:https://accounts.google.com/o/oauth2/v2/auth}")
    private String googleAuthorizeUrl;

    @Value("${auth.oauth2.google.token-url:https://oauth2.googleapis.com/token}")
    private String googleTokenUrl;

    @Value("${auth.oauth2.google.user-info-url:https://openidconnect.googleapis.com/v1/userinfo}")
    private String googleUserInfoUrl;

    @Value("${auth.oauth2.google.scope:openid profile email}")
    private String googleScope;

    @Value("${auth.oauth2.github.client-id:}")
    private String githubClientId;

    @Value("${auth.oauth2.github.client-secret:}")
    private String githubClientSecret;

    @Value("${auth.oauth2.github.authorize-url:https://github.com/login/oauth/authorize}")
    private String githubAuthorizeUrl;

    @Value("${auth.oauth2.github.token-url:https://github.com/login/oauth/access_token}")
    private String githubTokenUrl;

    @Value("${auth.oauth2.github.user-info-url:https://api.github.com/user}")
    private String githubUserInfoUrl;

    @Value("${auth.oauth2.github.user-emails-url:https://api.github.com/user/emails}")
    private String githubUserEmailsUrl;

    @Value("${auth.oauth2.github.scope:read:user user:email}")
    private String githubScope;

    // Base URL of this service's OAuth callback endpoint (registered in Google/GitHub consoles)
    @Value("${auth.oauth2.callback-base-url:http://localhost:8080/api/v1/auth/oauth/callback}")
    private String callbackBaseUrl;

    public AuthCommandService(JwtProperties jwtProperties,
                       UserWriteRepository userRepository,
                       AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       EmailVerificationService emailVerificationService,
                       RefreshTokenService refreshTokenService,
                       PermissionService permissionService,
                       TokenBlacklistService tokenBlacklistService,
                       OAuthStateService oauthStateService,
                       ApplicationEventPublisher applicationEvents) {
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.emailVerificationService = emailVerificationService;
        this.refreshTokenService = refreshTokenService;
        this.permissionService = permissionService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.oauthStateService = oauthStateService;
        this.applicationEvents = applicationEvents;
        // Configure timeouts: connect 5s, read 15s (within gateway's 20s TimeLimiter)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Transactional
    public RegisterResponse register(RegisterUserCommand command) {

        if (userRepository.existsByEmail(command.email())) {
            throw BusinessException.duplicateResource("User", command.email());
        }
        if (userRepository.existsByUsername(command.username())) {
            throw BusinessException.duplicateResource("User", command.username());
        }

        String hashedPassword = passwordEncoder.encode(command.password());

        var studentRole = roleRepository.findByCode("USER")
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Default USER role not found"));

        User user = User.builder()
                .email(command.email())
                .username(command.username())
                .passwordHash(hashedPassword)
                .fullName(command.fullName())
                .phone(command.phone())
                .status(Status.ACTIVE.getDescription())
                .emailVerified(false)
                .build();
        user.addRole(studentRole);
        userRepository.save(user);

        // Fire-and-forget events — failures must not break registration.
        try {
            UserRegisteredEvent registered = new UserRegisteredEvent();
            registered.setUserId(user.getId());
            registered.setEmail(user.getEmail());
            registered.setUsername(user.getUsername());
            registered.setFullName(user.getFullName());
            applicationEvents.publishEvent(registered);
        } catch (Exception e) {
            log.error("Failed to publish UserRegisteredEvent for userId={}", user.getId(), e);
        }

        try {
            String verificationToken = emailVerificationService.createVerificationToken(user);
            EmailVerificationRequestedEvent requested = new EmailVerificationRequestedEvent();
            requested.setUserId(user.getId());
            requested.setEmail(user.getEmail());
            requested.setVerificationToken(verificationToken);
            requested.setExpiresAt(LocalDateTime.now().plusHours(24));
            applicationEvents.publishEvent(requested);
        } catch (Exception e) {
            log.error("Failed to issue/publish email verification for user: {}", user.getEmail(), e);
        }
        return mapToRegisterResponse(user);
    }

    @Transactional
    public LoginResponse login(LoginUserCommand command) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            command.username(),
                            command.password()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw UnauthorizedException.invalidCredentials();
        }

        User user = userRepository.findByEmailOrUsernameWithRoles(command.username())
                .orElseThrow(UnauthorizedException::invalidCredentials);

        if (!Status.ACTIVE.getDescription().equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Account is disabled");
        }

        return issueLoginResponse(user);
    }

    @Transactional
    public OAuthAuthorizeResult getOauthAuthorizationUrl(StartOAuthAuthorizationCommand command) {
        OAuthProviderConfig config = resolveProvider(command.provider());
        // Validate and store the frontend callback URI — NOT sent to OAuth provider
        String normalizedRedirectUri = validateRedirectUri(command.redirectUri());
        // Backend generates state to prevent state mismatch
        String state = oauthStateService.generateAndStore(config.getProvider(), normalizedRedirectUri);

        // Backend callback URL registered in Google/GitHub developer consoles
        String backendCallbackUri = callbackBaseUrl + "/" + config.getProvider();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(config.getAuthorizationUrl())
                .queryParam("client_id", config.getClientId())
                .queryParam("redirect_uri", backendCallbackUri)
                .queryParam("scope", config.getScope())
                .queryParam("state", state)
                .queryParam("response_type", "code");

        if (PROVIDER_GOOGLE.equals(config.getProvider())) {
            builder.queryParam("access_type", "offline")
                    .queryParam("include_granted_scopes", "true")
                    .queryParam("prompt", "select_account");
        }
        if (PROVIDER_GITHUB.equals(config.getProvider())) {
            builder.queryParam("allow_signup", "true");
        }

        String url = builder.build().encode().toUriString();
        return new OAuthAuthorizeResult(url, state);
    }

    @Transactional
    public OAuthCallbackResult oauthCallback(CompleteOAuthCallbackCommand command) {
        OAuthProviderConfig config = resolveProvider(command.provider());
        if (command.state() == null || command.state().isBlank()) {
            throw BusinessException.invalidInput("Missing OAuth state");
        }
        if (command.code() == null || command.code().isBlank()) {
            throw BusinessException.invalidInput("Missing OAuth code");
        }

        // Atomically retrieve and delete the stored frontend redirect URI (one-time use)
        String frontendRedirectUri = oauthStateService.validateAndConsume(config.getProvider(), command.state());
        if (frontendRedirectUri == null) {
            throw new UnauthorizedException("Invalid or expired OAuth state");
        }
        // Use backend callback URL (must match what was registered in OAuth provider console)
        String backendCallbackUri = callbackBaseUrl + "/" + config.getProvider();

        String accessToken = exchangeCodeForAccessToken(config, command.code(), backendCallbackUri);
        OAuthUserProfile profile = fetchUserProfile(config, accessToken);

        if (profile.email() == null || profile.email().isBlank()) {
            throw new UnauthorizedException("Unable to verify email from OAuth provider");
        }

        User user = findOrCreateSocialUser(profile);
        if (!Status.ACTIVE.getDescription().equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Account is disabled");
        }

        LoginResponse loginResponse = issueLoginResponse(user);
        return new OAuthCallbackResult(loginResponse, frontendRedirectUri);
    }

    @Transactional
    public LoginResponse refreshToken(RefreshAccessTokenCommand command) {

        User user = refreshTokenService.validateRefreshToken(command.refreshToken());

        user = userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(UnauthorizedException::invalidCredentials);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
        Set<String> userPermissions = user.getAllPermissions();
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), roles, userPermissions);

        Set<String> permissions = permissionService.getUserPermissions(user.getId());
        return buildLoginResponse(user, accessToken, command.refreshToken(), permissions);
    }

    @Transactional
    public void logout(LogoutCommand command) {
        String token = command.authorizationHeader().substring("Bearer ".length());
        Long userId = jwtProvider.getUserIdFromToken(token);

        // Blacklist the current access token so gateway rejects it immediately
        try {
            String jti = jwtProvider.getJtiFromToken(token);
            long remainingTtl = (jwtProvider.getExpirationFromToken(token).getTime() - System.currentTimeMillis()) / 1000;
            tokenBlacklistService.blacklistToken(jti, remainingTtl);
            log.debug("Access token blacklisted: jti={} ttl={}s", jti, remainingTtl);
        } catch (Exception e) {
            log.warn("Failed to blacklist access token: {}", e.getMessage());
        }

        // Revoke all refresh tokens from DB
        refreshTokenService.revokeUserTokens(userId);
    }

    @Transactional
    public void verifyEmail(VerifyEmailCommand command) {
        emailVerificationService.verifyEmail(command.token());
    }

    private RegisterResponse mapToRegisterResponse(User user) {

        List<RegisterResponse.RoleInfo> roles = user.getRoles().stream()
                .map(role -> RegisterResponse.RoleInfo.builder()
                        .id(role.getId())
                        .code(role.getCode())
                        .name(role.getName())
                        .isPrimary(true)
                        .build())
                .collect(Collectors.toList());

        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken, Set<String> permissions) {

        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatar(user.getAvatarUrl())
                .roles(user.getRoles().stream()
                        .map(role -> LoginResponse.RoleInfo.builder()
                                .id(role.getId())
                                .code(role.getCode())
                                .name(role.getName())
                                .isPrimary(true)
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LoginResponse.TokenInfo tokenInfo = LoginResponse.TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();

        return LoginResponse.builder()
                .user(userInfo)
                .tokens(tokenInfo)
                .permissions(permissions)
                .build();
    }

    private LoginResponse issueLoginResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
        Set<String> userPermissions = user.getAllPermissions();

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), roles, userPermissions);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getUsername());
        RefreshToken rt = refreshTokenService.createRefreshToken(user, refreshToken);

        Set<String> permissions = permissionService.getUserPermissions(user.getId());
        return buildLoginResponse(user, accessToken, rt.getToken(), permissions);
    }

    private String validateRedirectUri(String redirectUri) {
        if (redirectUri == null || redirectUri.isBlank()) {
            throw BusinessException.invalidInput("Missing redirectUri");
        }

        URI uri = parseUri(redirectUri.trim());
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw BusinessException.invalidInput("Invalid redirectUri");
        }
        if (!OAUTH_CALLBACK_PATH.equals(uri.getPath())) {
            throw new UnauthorizedException("Invalid redirect URI path");
        }

        String normalizedOrigin = normalizedOrigin(uri);
        String[] allowlist = oauthRedirectAllowlist.split(",");
        boolean allowed = false;
        for (String base : allowlist) {
            String value = base.trim();
            if (value.isEmpty()) {
                continue;
            }
            URI allowedUri = parseUri(value);
            String allowedOrigin = normalizedOrigin(allowedUri);
            if (normalizedOrigin.equals(allowedOrigin)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new UnauthorizedException("Redirect URI is not allowed");
        }

        return uri.toString();
    }

    private URI parseUri(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException ex) {
            throw BusinessException.invalidInput("Invalid redirectUri");
        }
    }

    private String normalizedOrigin(URI uri) {
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        int port = uri.getPort();
        if (port < 0) {
            if ("http".equals(scheme)) {
                port = 80;
            } else if ("https".equals(scheme)) {
                port = 443;
            }
        }
        return scheme + "://" + host + ":" + port;
    }

    private OAuthProviderConfig resolveProvider(String provider) {
        String normalized = provider == null ? "" : provider.trim().toLowerCase(Locale.ROOT);
        if (PROVIDER_GOOGLE.equals(normalized)) {
            validateProviderConfig(PROVIDER_GOOGLE, googleClientId, googleClientSecret);
            return new OAuthProviderConfig(PROVIDER_GOOGLE, googleClientId, googleClientSecret,
                    googleAuthorizeUrl, googleTokenUrl, googleUserInfoUrl, googleScope);
        }
        if (PROVIDER_GITHUB.equals(normalized)) {
            validateProviderConfig(PROVIDER_GITHUB, githubClientId, githubClientSecret);
            return new OAuthProviderConfig(PROVIDER_GITHUB, githubClientId, githubClientSecret,
                    githubAuthorizeUrl, githubTokenUrl, githubUserInfoUrl, githubScope);
        }
        throw BusinessException.invalidInput("Unsupported OAuth provider: " + provider);
    }

    private void validateProviderConfig(String provider, String clientId, String clientSecret) {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw BusinessException.operationFailed("oauth-config-" + provider);
        }
    }

    private String exchangeCodeForAccessToken(OAuthProviderConfig config, String code, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", config.getClientId());
        form.add("client_secret", config.getClientSecret());
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        if (PROVIDER_GOOGLE.equals(config.getProvider())) {
            form.add("grant_type", "authorization_code");
        }

        ResponseEntity<Map> tokenResponse;
        try {
            tokenResponse = restTemplate.postForEntity(config.getTokenUrl(), new HttpEntity<>(form, headers), Map.class);
        } catch (Exception ex) {
            log.error("OAuth token exchange failed for provider {}", config.getProvider(), ex);
            throw new UnauthorizedException("OAuth token exchange failed");
        }

        Object token = tokenResponse.getBody() == null ? null : tokenResponse.getBody().get("access_token");
        if (!(token instanceof String accessToken) || accessToken.isBlank()) {
            throw new UnauthorizedException("OAuth access token missing");
        }
        return accessToken;
    }

    private OAuthUserProfile fetchUserProfile(OAuthProviderConfig config, String accessToken) {
        if (PROVIDER_GOOGLE.equals(config.getProvider())) {
            return fetchGoogleProfile(config.getUserInfoUrl(), accessToken);
        }
        return fetchGithubProfile(config.getUserInfoUrl(), accessToken);
    }

    private OAuthUserProfile fetchGoogleProfile(String userInfoUrl, String accessToken) {
        Map<String, Object> profileData = fetchJsonWithBearer(userInfoUrl, accessToken);

        String email = asString(profileData.get("email"));
        boolean emailVerified = Boolean.TRUE.equals(profileData.get("email_verified"));
        String fullName = asString(profileData.get("name"));
        String avatarUrl = asString(profileData.get("picture"));
        String usernameHint = asString(profileData.get("given_name"));

        return new OAuthUserProfile(email, fullName, avatarUrl, usernameHint, emailVerified);
    }

    private OAuthUserProfile fetchGithubProfile(String userInfoUrl, String accessToken) {
        Map<String, Object> profileData = fetchJsonWithBearer(userInfoUrl, accessToken);

        EmailResult emailResult = resolveGithubEmail(profileData, accessToken);

        String fullName = asString(profileData.get("name"));
        String avatarUrl = asString(profileData.get("avatar_url"));
        String usernameHint = asString(profileData.get("login"));

        return new OAuthUserProfile(emailResult.email(), fullName, avatarUrl, usernameHint, emailResult.verified());
    }

    private EmailResult resolveGithubEmail(Map<String, Object> profileData, String accessToken) {
        String profileEmail = asString(profileData.get("email"));
        List<Map<String, Object>> emails = fetchGithubEmails(accessToken);

        for (Map<String, Object> emailObj : emails) {
            String email = asString(emailObj.get("email"));
            boolean verified = Boolean.TRUE.equals(emailObj.get("verified"));
            boolean primary = Boolean.TRUE.equals(emailObj.get("primary"));
            if (primary && verified && email != null && !email.isBlank()) {
                return new EmailResult(email, true);
            }
        }

        if (profileEmail != null && !profileEmail.isBlank()) {
            for (Map<String, Object> emailObj : emails) {
                String email = asString(emailObj.get("email"));
                boolean verified = Boolean.TRUE.equals(emailObj.get("verified"));
                if (verified && profileEmail.equalsIgnoreCase(email)) {
                    return new EmailResult(profileEmail, true);
                }
            }
        }

        for (Map<String, Object> emailObj : emails) {
            String email = asString(emailObj.get("email"));
            boolean verified = Boolean.TRUE.equals(emailObj.get("verified"));
            if (verified && email != null && !email.isBlank()) {
                return new EmailResult(email, true);
            }
        }

        if (profileEmail != null && !profileEmail.isBlank()) {
            return new EmailResult(profileEmail, false);
        }

        return new EmailResult(null, false);
    }

    private List<Map<String, Object>> fetchGithubEmails(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "certimaster-auth-service");

        ResponseEntity<List> response;
        try {
            response = restTemplate.exchange(githubUserEmailsUrl, HttpMethod.GET, new HttpEntity<>(headers), List.class);
        } catch (Exception ex) {
            log.warn("Failed to fetch GitHub user emails", ex);
            return List.of();
        }

        if (response.getBody() == null) {
            return List.of();
        }

        List<Map<String, Object>> results = new java.util.ArrayList<>();
        for (Object item : response.getBody()) {
            if (!(item instanceof Map<?, ?> emailObj)) {
                continue;
            }
            Map<String, Object> normalized = new java.util.HashMap<>();
            normalized.put("email", emailObj.get("email"));
            normalized.put("verified", emailObj.get("verified"));
            normalized.put("primary", emailObj.get("primary"));
            results.add(normalized);
        }

        return results;
    }

    private Map<String, Object> fetchJsonWithBearer(String url, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "certimaster-auth-service");

        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        } catch (Exception ex) {
            throw new UnauthorizedException("Failed to fetch OAuth profile", ex);
        }

        if (response.getBody() == null) {
            throw new UnauthorizedException("OAuth profile response is empty");
        }

        Map<String, Object> result = response.getBody();
        return result;
    }

    private User findOrCreateSocialUser(OAuthUserProfile profile) {
        Optional<User> existingOpt = userRepository.findByEmailWithRoles(profile.email());
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();

            if (!Status.ACTIVE.getDescription().equalsIgnoreCase(existing.getStatus())) {
                throw new UnauthorizedException("Account is disabled");
            }

            boolean changed = false;

            if (profile.fullName() != null && !profile.fullName().isBlank() && !profile.fullName().equals(existing.getFullName())) {
                existing.setFullName(profile.fullName());
                changed = true;
            }
            if (profile.avatarUrl() != null && !profile.avatarUrl().isBlank() && !profile.avatarUrl().equals(existing.getAvatarUrl())) {
                existing.setAvatarUrl(profile.avatarUrl());
                changed = true;
            }
            if (profile.emailVerified() && Boolean.FALSE.equals(existing.getEmailVerified())) {
                existing.setEmailVerified(true);
                changed = true;
            }

            if (existing.getRoles() == null || existing.getRoles().isEmpty()) {
                existing.addRole(getDefaultUserRole());
                changed = true;
            }

            if (changed) {
                return userRepository.save(existing);
            }
            return existing;
        }

        User user = User.builder()
                .email(profile.email())
                .username(generateUniqueUsername(profile))
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .fullName(profile.fullName())
                .avatarUrl(profile.avatarUrl())
                .status(Status.ACTIVE.getDescription())
                .emailVerified(profile.emailVerified())
                .roles(new java.util.HashSet<>())
                .build();
        user.addRole(getDefaultUserRole());

        return userRepository.save(user);
    }

    private Role getDefaultUserRole() {
        return roleRepository.findByCode("USER")
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Default USER role not found"));
    }

    private String generateUniqueUsername(OAuthUserProfile profile) {
        String source = profile.usernameHint();
        if (source == null || source.isBlank()) {
            source = profile.email();
        }

        String normalized = source.toLowerCase(Locale.ROOT)
                .replaceAll("@.*$", "")
                .replaceAll("[^a-z0-9._-]", "");

        if (normalized.isBlank()) {
            normalized = "user";
        }

        if (!userRepository.existsByUsernameIgnoreCase(normalized)) {
            return normalized;
        }

        for (int i = 1; i <= 1000; i++) {
            String candidate = normalized + i;
            if (!userRepository.existsByUsernameIgnoreCase(candidate)) {
                return candidate;
            }
        }

        return normalized + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }
}
