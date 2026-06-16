package com.certimaster.shared.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:3001,https://certimaster.com}")
    private String corsAllowedOrigins;

    @Value("${security.cors.allowed-origin-patterns:}")
    private String corsAllowedOriginPatterns;

    @Value("${security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String corsAllowedMethods;

    @Value("${security.cors.allowed-headers:Authorization,Content-Type,X-User-Id,X-Requested-With,Accept,Origin}")
    private String corsAllowedHeaders;

    @Value("${security.cors.exposed-headers:Authorization,X-User-Id}")
    private String corsExposedHeaders;

    @Value("${security.cors.allow-credentials:true}")
    private boolean corsAllowCredentials;

    @Value("${security.cors.max-age-seconds:3600}")
    private long corsMaxAgeSeconds;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/oauth/**",
            "/api/v1/auth/refresh",
            "/api/v1/auth/verify-email",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password"
    };

    private static final String[] ACTUATOR_ENDPOINTS = {
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/health",
            "/health/**",
            "/bootui/**"
    };

    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/lookups",
            "/api/v1/lookups/**",
            "/api/v1/certifications",
            "/api/v1/certifications/**",
            "/api/v1/providers",
            "/api/v1/providers/**",
            "/api/v1/posts",
            "/api/v1/posts/{slug}",
            "/api/v1/posts/{id}/view",
            "/api/v1/posts/trending",
            "/api/v1/posts/featured",
            "/api/v1/posts/{postId}/related",
            "/api/v1/categories",
            "/api/v1/categories/**",
            "/api/v1/tags",
            "/api/v1/tags/**",
            "/api/v1/series",
            "/api/v1/series/**",
            "/api/v1/posts/{postId}/comments"
    };

    // Admin endpoints are guarded by a single pattern below ("/api/v1/admin/**").
    // Method-level @PreAuthorize in controllers adds the role check as defense-in-depth.

    private static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api-docs/**"
    };

    public SecurityConfig(JwtProvider jwtProvider, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> originPatterns = csv(corsAllowedOriginPatterns);
        if (originPatterns.isEmpty()) {
            configuration.setAllowedOrigins(csv(corsAllowedOrigins));
        } else {
            configuration.setAllowedOriginPatterns(originPatterns);
        }
        configuration.setAllowedMethods(csv(corsAllowedMethods));
        configuration.setAllowedHeaders(csv(corsAllowedHeaders));
        configuration.setAllowCredentials(corsAllowCredentials);
        configuration.setMaxAge(corsMaxAgeSeconds);
        configuration.setExposedHeaders(csv(corsExposedHeaders));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }

    private List<String> csv(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(ACTUATOR_ENDPOINTS).permitAll()
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/view").permitAll()
                        .requestMatchers("/api/v1/internal/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/current").authenticated()
                        .requestMatchers("/api/v1/profile/me/**").authenticated()
                        .requestMatchers("/api/v1/profile/{userId}").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                
                // Exception handling for authentication/authorization errors
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                writeProblem(response, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED",
                                        "Authentication required", request.getRequestURI(), request.getMethod()))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeProblem(response, HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                                        "Access denied to this resource", request.getRequestURI(), request.getMethod()))
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeProblem(HttpServletResponse response, HttpStatus status, String errorCode,
                              String detail, String path, String method) throws IOException {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("urn:problem-type:" + errorCode.toLowerCase().replace('_', '-')));
        problem.setTitle(status.getReasonPhrase());
        problem.setInstance(URI.create(path));
        problem.setProperty("errorCode", errorCode);
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("method", method);
        String correlationId = MDC.get("correlationId");
        if (correlationId != null && !correlationId.isBlank()) {
            problem.setProperty("correlationId", correlationId);
        }

        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
