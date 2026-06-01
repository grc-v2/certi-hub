package com.certimaster.identity.adapter.web;

import com.certimaster.shared.exception.UnauthorizedException;
import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.identity.adapter.web.request.ForgotPasswordRequest;
import com.certimaster.identity.adapter.web.request.LoginRequest;
import com.certimaster.identity.application.command.CompleteOAuthCallbackCommand;
import com.certimaster.identity.application.command.LoginUserCommand;
import com.certimaster.identity.application.command.LogoutCommand;
import com.certimaster.identity.application.command.PasswordResetService;
import com.certimaster.identity.application.command.RefreshAccessTokenCommand;
import com.certimaster.identity.application.command.RegisterUserCommand;
import com.certimaster.identity.application.command.RequestPasswordResetCommand;
import com.certimaster.identity.application.command.ResetPasswordCommand;
import com.certimaster.identity.application.command.StartOAuthAuthorizationCommand;
import com.certimaster.identity.application.command.VerifyEmailCommand;
import com.certimaster.identity.application.query.AuthQueryService;
import com.certimaster.identity.application.query.GetCurrentUserQuery;
import com.certimaster.identity.adapter.web.request.RefreshTokenRequest;
import com.certimaster.identity.adapter.web.request.RegisterRequest;
import com.certimaster.identity.adapter.web.request.ResetPasswordRequest;
import com.certimaster.identity.adapter.web.response.LoginResponse;
import com.certimaster.identity.adapter.web.response.RegisterResponse;
import com.certimaster.identity.adapter.web.response.OAuthAuthorizeResult;
import com.certimaster.identity.adapter.web.response.OAuthCallbackResult;
import com.certimaster.identity.adapter.web.response.UserResponse;
import com.certimaster.identity.application.command.AuthCommandService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;
    private final AuthCookieService authCookieService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthCommandService authCommandService,
                          AuthQueryService authQueryService,
                          AuthCookieService authCookieService,
                          PasswordResetService passwordResetService) {
        this.authCommandService = authCommandService;
        this.authQueryService = authQueryService;
        this.authCookieService = authCookieService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authCommandService.register(toRegisterCommand(registerRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        LoginResponse loginResponse = authCommandService.login(toLoginCommand(loginRequest));
        authCookieService.setRefreshCookie(response, loginResponse.getTokens().getRefreshToken());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/oauth/authorize/{provider}")
    public ResponseEntity<Map<String, String>> getOauthAuthorizationUrl(
            @PathVariable String provider,
            @RequestParam String redirectUri
    ) {
        OAuthAuthorizeResult result = authCommandService.getOauthAuthorizationUrl(
                new StartOAuthAuthorizationCommand(provider, redirectUri)
        );
        return ResponseEntity.ok(Map.of("url", result.url(), "state", result.state()));
    }

    // Called directly by Google/GitHub as the registered redirect URI after user authenticates.
    // Exchanges the auth code for tokens, then redirects the browser to the frontend.
    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<Void> oauthCallback(
            @PathVariable String provider,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpServletResponse response
    ) {
        if (error != null) {
            log.warn("OAuth denied/error from provider {}: {}", provider, error);
            URI errorRedirect = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                    .queryParam("error", "oauth_denied")
                    .queryParam("provider", provider)
                    .build().toUri();
            return ResponseEntity.status(HttpStatus.FOUND).location(errorRedirect).build();
        }

        try {
            OAuthCallbackResult result = authCommandService.oauthCallback(
                    new CompleteOAuthCallbackCommand(provider, code, state)
            );
            LoginResponse loginResponse = result.loginResponse();

            authCookieService.setRefreshCookie(response, loginResponse.getTokens().getRefreshToken());

            URI redirectTo = UriComponentsBuilder.fromUriString(result.frontendRedirectUri())
                    .queryParam("accessToken", loginResponse.getTokens().getAccessToken())
                    .queryParam("refreshToken", loginResponse.getTokens().getRefreshToken())
                    .build()
                    .encode()
                    .toUri();

            return ResponseEntity.status(HttpStatus.FOUND).location(redirectTo).build();
        } catch (UnauthorizedException ex) {
            log.warn("OAuth callback failed for provider {}: {}", provider, ex.getMessage());
            URI errorRedirect = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                    .queryParam("error", "invalid_state")
                    .queryParam("provider", provider)
                    .build().toUri();
            return ResponseEntity.status(HttpStatus.FOUND).location(errorRedirect).build();
        }
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(authQueryService.getCurrentUser(new GetCurrentUserQuery(userId)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletRequest request,
            HttpServletResponse response) {
        String token = authCookieService.readRefreshCookie(request)
                .orElseGet(() -> body == null ? null : body.getRefreshToken());
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }
        LoginResponse loginResponse = authCommandService.refreshToken(new RefreshAccessTokenCommand(token));
        authCookieService.setRefreshCookie(response, loginResponse.getTokens().getRefreshToken());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorization,
            HttpServletResponse response) {
        authCommandService.logout(new LogoutCommand(authorization));
        authCookieService.clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        authCommandService.verifyEmail(new VerifyEmailCommand(token));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Always 204 to avoid account enumeration — the service no-ops on unknown email.
        passwordResetService.requestReset(new RequestPasswordResetCommand(request.getEmail()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(new ResetPasswordCommand(request.getToken(), request.getNewPassword()));
        return ResponseEntity.noContent().build();
    }

    private RegisterUserCommand toRegisterCommand(RegisterRequest request) {
        return new RegisterUserCommand(
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                request.getFullName(),
                request.getPhone()
        );
    }

    private LoginUserCommand toLoginCommand(LoginRequest request) {
        return new LoginUserCommand(request.getUsername(), request.getPassword());
    }
}
