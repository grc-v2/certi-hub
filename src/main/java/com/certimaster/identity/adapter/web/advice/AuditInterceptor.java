package com.certimaster.identity.adapter.web.advice;

import com.certimaster.shared.security.JwtUserPrincipal;
import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.identity.domain.entity.AuditLog;
import com.certimaster.identity.application.command.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "audit.startTimeNanos";
    private static final int MAX_UA_LEN = 500;
    private static final int MAX_PATH_LEN = 500;
    private static final int MAX_QS_LEN = 1000;

    private final AuditLogService auditLogService;
    private final String serviceName;

    public AuditInterceptor(
            AuditLogService auditLogService,
            @Value("${spring.application.name:unknown}") String serviceName) {
        this.auditLogService = auditLogService;
        this.serviceName = serviceName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        request.setAttribute(START_TIME_ATTR, System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        try {
            AuditLog entry = new AuditLog();

            JwtUserPrincipal principal = SecurityUtils.getCurrentUser();
            if (principal != null) {
                entry.setActorId(principal.getUserId());
                entry.setActorUsername(principal.getUsername());
            }

            entry.setAction(resolveAction(handler, request));
            entry.setHttpMethod(request.getMethod());
            entry.setHttpPath(truncate(request.getRequestURI(), MAX_PATH_LEN));
            entry.setQueryString(truncate(request.getQueryString(), MAX_QS_LEN));
            entry.setIpAddress(resolveIp(request));
            entry.setUserAgent(truncate(request.getHeader("User-Agent"), MAX_UA_LEN));
            entry.setStatusCode(response.getStatus());
            entry.setServiceName(serviceName);
            entry.setOccurredAt(LocalDateTime.now());

            Long startNanos = (Long) request.getAttribute(START_TIME_ATTR);
            if (startNanos != null) {
                entry.setDurationMs((System.nanoTime() - startNanos) / 1_000_000);
            }

            auditLogService.record(entry);
        } catch (Exception suppressed) {
            // Never fail the request because of audit logging.
        }
    }

    private String resolveAction(Object handler, HttpServletRequest request) {
        if (handler instanceof HandlerMethod hm) {
            return hm.getBeanType().getSimpleName() + "#" + hm.getMethod().getName();
        }
        return request.getMethod() + " " + request.getRequestURI();
    }

    private String resolveIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }
        return request.getRemoteAddr();
    }

    private String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() > max ? value.substring(0, max) : value;
    }
}
