package com.certimaster.identity.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.UnauthorizedException;
import com.certimaster.shared.security.JwtProperties;
import com.certimaster.shared.security.JwtProvider;
import com.certimaster.identity.adapter.web.response.LoginResponse;
import com.certimaster.identity.adapter.web.response.RegisterResponse;
import com.certimaster.identity.application.query.PermissionService;
import com.certimaster.identity.domain.entity.RefreshToken;
import com.certimaster.identity.domain.service.EmailVerificationService;
import com.certimaster.identity.domain.service.OAuthStateService;
import com.certimaster.identity.domain.service.RefreshTokenService;
import com.certimaster.identity.domain.service.TokenBlacklistService;
import com.certimaster.identity.adapter.persistence.RoleRepository;
import com.certimaster.identity.domain.entity.Role;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.domain.event.EmailVerificationRequestedEvent;
import com.certimaster.identity.domain.event.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceTest {

    @Mock
    private UserWriteRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private OAuthStateService oauthStateService;

    @Mock
    private ApplicationEventPublisher applicationEvents;

    private AuthCommandService service;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setAccessTokenExpiration(900000L);
        service = new AuthCommandService(
                jwtProperties,
                userRepository,
                authenticationManager,
                jwtProvider,
                passwordEncoder,
                roleRepository,
                emailVerificationService,
                refreshTokenService,
                permissionService,
                tokenBlacklistService,
                oauthStateService,
                applicationEvents
        );
    }

    @Test
    void registerCreatesUserAssignsDefaultRoleAndPublishesEvents() {
        Role userRole = userRole();
        when(userRepository.existsByEmail("learner@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("learner")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
        when(roleRepository.findByCode("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            saved.setCreatedAt(LocalDateTime.parse("2026-05-25T09:30:00"));
            return saved;
        });
        when(emailVerificationService.createVerificationToken(any(User.class))).thenReturn("verify-token");

        RegisterResponse response = service.register(new RegisterUserCommand(
                "learner@example.com",
                "learner",
                "secret",
                "Learner User",
                "0900000000"
        ));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("learner@example.com");
        assertThat(savedUser.getUsername()).isEqualTo("learner");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed-secret");
        assertThat(savedUser.getEmailVerified()).isFalse();
        assertThat(savedUser.getRoles()).extracting(Role::getCode).containsExactly("USER");

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(applicationEvents, times(2)).publishEvent(eventCaptor.capture());
        UserRegisteredEvent registeredEvent = eventCaptor.getAllValues().stream()
                .filter(UserRegisteredEvent.class::isInstance)
                .map(UserRegisteredEvent.class::cast)
                .findFirst()
                .orElseThrow();
        assertThat(registeredEvent.getUserId()).isEqualTo(42L);
        assertThat(registeredEvent.getEventVersion()).isEqualTo(1);
        assertThat(registeredEvent.getEventId()).isNotBlank();

        EmailVerificationRequestedEvent verificationEvent = eventCaptor.getAllValues().stream()
                .filter(EmailVerificationRequestedEvent.class::isInstance)
                .map(EmailVerificationRequestedEvent.class::cast)
                .findFirst()
                .orElseThrow();
        assertThat(verificationEvent.getUserId()).isEqualTo(42L);
        assertThat(verificationEvent.getVerificationToken()).isEqualTo("verify-token");
        assertThat(verificationEvent.getEventVersion()).isEqualTo(1);
        assertThat(verificationEvent.getEventId()).isNotBlank();

        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getEmail()).isEqualTo("learner@example.com");
        assertThat(response.getRoles()).extracting(RegisterResponse.RoleInfo::getCode).containsExactly("USER");
    }

    @Test
    void registerFailsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("learner@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(new RegisterUserCommand(
                "learner@example.com",
                "learner",
                "secret",
                "Learner User",
                null
        ))).isInstanceOf(BusinessException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(applicationEvents, never()).publishEvent(any());
    }

    @Test
    void loginAuthenticatesUserAndIssuesAccessAndRefreshTokens() {
        User user = activeUser();
        when(userRepository.findByEmailOrUsernameWithRoles("learner")).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(
                eq(42L),
                eq("learner"),
                eq("learner@example.com"),
                eq(Set.of("USER")),
                eq(Set.of("certification:read"))
        )).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(42L, "learner")).thenReturn("jwt-refresh-token");
        when(refreshTokenService.createRefreshToken(user, "jwt-refresh-token"))
                .thenReturn(RefreshToken.builder()
                        .userId(42L)
                        .token("stored-refresh-token")
                        .expiresAt(LocalDateTime.now().plusDays(7))
                        .build());
        when(permissionService.getUserPermissions(42L)).thenReturn(Set.of("certification:read"));

        LoginResponse response = service.login(new LoginUserCommand("learner", "secret"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getUser().getId()).isEqualTo(42L);
        assertThat(response.getUser().getEmail()).isEqualTo("learner@example.com");
        assertThat(response.getUser().getRoles()).extracting(LoginResponse.RoleInfo::getCode).containsExactly("USER");
        assertThat(response.getTokens().getAccessToken()).isEqualTo("access-token");
        assertThat(response.getTokens().getRefreshToken()).isEqualTo("stored-refresh-token");
        assertThat(response.getTokens().getTokenType()).isEqualTo("Bearer");
        assertThat(response.getTokens().getExpiresIn()).isEqualTo(900L);
        assertThat(response.getPermissions()).containsExactly("certification:read");
    }

    @Test
    void loginFailsWhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThatThrownBy(() -> service.login(new LoginUserCommand("learner", "wrong")))
                .isInstanceOf(UnauthorizedException.class);

        verify(userRepository, never()).findByEmailOrUsernameWithRoles(any());
        verify(refreshTokenService, never()).createRefreshToken(any(User.class), any());
    }

    @Test
    void loginFailsWhenAccountIsDisabled() {
        User user = activeUser();
        user.setStatus("DISABLED");
        when(userRepository.findByEmailOrUsernameWithRoles("learner")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.login(new LoginUserCommand("learner", "secret")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Account is disabled");

        verify(refreshTokenService, never()).createRefreshToken(any(User.class), any());
    }

    @Test
    void refreshTokenValidatesRefreshTokenAndReusesItInResponse() {
        User tokenUser = User.builder().build();
        tokenUser.setId(42L);
        User user = activeUser();
        when(refreshTokenService.validateRefreshToken("stored-refresh-token")).thenReturn(tokenUser);
        when(userRepository.findByIdWithRoles(42L)).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(
                eq(42L),
                eq("learner"),
                eq("learner@example.com"),
                eq(Set.of("USER")),
                eq(Set.of("certification:read"))
        )).thenReturn("new-access-token");
        when(permissionService.getUserPermissions(42L)).thenReturn(Set.of("certification:read"));

        LoginResponse response = service.refreshToken(new RefreshAccessTokenCommand("stored-refresh-token"));

        assertThat(response.getUser().getId()).isEqualTo(42L);
        assertThat(response.getTokens().getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getTokens().getRefreshToken()).isEqualTo("stored-refresh-token");
        verify(refreshTokenService).validateRefreshToken("stored-refresh-token");
    }

    @Test
    void oauthAuthorizationRejectsUnsupportedProvider() {
        assertThatThrownBy(() -> service.getOauthAuthorizationUrl(new StartOAuthAuthorizationCommand(
                "linkedin",
                "http://localhost:3000/oauth/callback"
        ))).isInstanceOf(BusinessException.class)
                .hasMessageContaining("Unsupported OAuth provider");

        verify(oauthStateService, never()).generateAndStore(any(), any());
    }

    @Test
    void oauthAuthorizationRejectsRedirectUriOutsideCallbackPath() {
        configureGoogleOauth();

        assertThatThrownBy(() -> service.getOauthAuthorizationUrl(new StartOAuthAuthorizationCommand(
                "google",
                "http://localhost:3000/not-oauth"
        ))).isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid redirect URI path");

        verify(oauthStateService, never()).generateAndStore(any(), any());
    }

    private void configureGoogleOauth() {
        ReflectionTestUtils.setField(service, "oauthRedirectAllowlist", "http://localhost:3000");
        ReflectionTestUtils.setField(service, "googleClientId", "google-client-id");
        ReflectionTestUtils.setField(service, "googleClientSecret", "google-client-secret");
        ReflectionTestUtils.setField(service, "googleAuthorizeUrl", "https://accounts.google.com/o/oauth2/v2/auth");
        ReflectionTestUtils.setField(service, "googleTokenUrl", "https://oauth2.googleapis.com/token");
        ReflectionTestUtils.setField(service, "googleUserInfoUrl", "https://openidconnect.googleapis.com/v1/userinfo");
        ReflectionTestUtils.setField(service, "googleScope", "openid profile email");
        ReflectionTestUtils.setField(service, "callbackBaseUrl", "http://localhost:8080/api/v1/auth/oauth/callback");
    }

    private User activeUser() {
        Role role = userRole();
        User user = User.builder()
                .email("learner@example.com")
                .username("learner")
                .passwordHash("hashed")
                .fullName("Learner User")
                .status("ACTIVE")
                .emailVerified(true)
                .roles(new java.util.HashSet<>())
                .build();
        user.setId(42L);
        user.addRole(role);
        return user;
    }

    private Role userRole() {
        Role role = Role.builder()
                .code("USER")
                .name("User")
                .permissions(Set.of("certification:read"))
                .build();
        role.setId(7L);
        return role;
    }
}
