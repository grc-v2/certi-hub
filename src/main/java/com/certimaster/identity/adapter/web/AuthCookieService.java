package com.certimaster.identity.adapter.web;

import com.certimaster.shared.security.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Builds and clears the refresh-token cookie used by the FE.
 * Cookie is HttpOnly; client JS never touches it.
 */
@Component
public class AuthCookieService {

    public static final String REFRESH_COOKIE = "certi_refresh";
    public static final String REFRESH_COOKIE_PATH = "/api/v1/auth";

    private final JwtProperties jwtProperties;
    private final boolean secure;
    private final String sameSite;
    private final String domain;

    public AuthCookieService(
            JwtProperties jwtProperties,
            @Value("${auth.cookie.secure:false}") boolean secure,
            @Value("${auth.cookie.same-site:Lax}") String sameSite,
            @Value("${auth.cookie.domain:}") String domain) {
        this.jwtProperties = jwtProperties;
        this.secure = secure;
        this.sameSite = sameSite;
        this.domain = domain;
    }

    public void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()));
        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.ZERO);
        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    public Optional<String> readRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        for (Cookie c : cookies) {
            if (REFRESH_COOKIE.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                return Optional.of(c.getValue());
            }
        }
        return Optional.empty();
    }
}
