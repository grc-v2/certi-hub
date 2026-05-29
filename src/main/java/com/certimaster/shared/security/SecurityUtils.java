package com.certimaster.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static JwtUserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
            return principal;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        JwtUserPrincipal user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentUsername() {
        JwtUserPrincipal user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    public static boolean hasRole(String role) {
        JwtUserPrincipal user = getCurrentUser();
        return user != null && user.hasRole(role);
    }

    public static boolean hasPermission(String permission) {
        JwtUserPrincipal user = getCurrentUser();
        return user != null && user.hasPermission(permission);
    }

    public static boolean isAdmin() { return hasRole("ADMIN"); }
    public static boolean isInstructor() { return hasRole("INSTRUCTOR"); }
    public static boolean isStudent() { return hasRole("USER"); }
}
