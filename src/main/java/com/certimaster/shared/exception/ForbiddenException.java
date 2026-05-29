package com.certimaster.shared.exception;

import java.util.Map;

/**
 * Exception for authorization failures
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }

    public ForbiddenException(String message, Map<String, Object> details) {
        super("FORBIDDEN", message, details);
    }

    public ForbiddenException(String message, Throwable cause) {
        super("FORBIDDEN", message, cause);
    }

    // Factory methods

    public static ForbiddenException accessDenied() {
        return new ForbiddenException("Access denied to this resource");
    }

    public static ForbiddenException insufficientPermissions() {
        return new ForbiddenException("Insufficient permissions to perform this action");
    }

    public static ForbiddenException resourceOwnerOnly() {
        return new ForbiddenException("Only the resource owner can perform this action");
    }

    public static ForbiddenException roleRequired(String role) {
        ForbiddenException ex = new ForbiddenException(
                String.format("Role '%s' is required to access this resource", role));
        ex.addDetail("requiredRole", role);
        return ex;
    }
}
