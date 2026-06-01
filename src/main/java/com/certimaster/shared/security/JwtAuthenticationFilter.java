package com.certimaster.shared.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(BEARER_PREFIX.length());
            Claims claims = jwtProvider.validateToken(jwt);

            if (!"access".equals(claims.get("type", String.class))) {
                log.warn("Rejected non-access token type for authentication");
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Collection<GrantedAuthority> authorities = buildAuthorities(claims);
                JwtUserPrincipal principal = new JwtUserPrincipal(userId, username, authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        } catch (Exception e) {
            // Invalid credentials stay unauthenticated; authorization handles protected endpoints.
        }

        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> buildAuthorities(Claims claims) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = claims.get("roles", List.class);
        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }

        List<String> permissions = claims.get("permissions", List.class);
        if (permissions != null) {
            permissions.forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));
        }

        return authorities;
    }
}
