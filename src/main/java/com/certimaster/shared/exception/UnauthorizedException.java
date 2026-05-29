package com.certimaster.shared.exception;

import java.util.Map;

/**
 * Exception for authentication failures
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }

    public UnauthorizedException(String message, Map<String, Object> details) {
        super("UNAUTHORIZED", message, details);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super("UNAUTHORIZED", message, cause);
    }

    // Factory methods

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Invalid username or password");
    }

    public static UnauthorizedException tokenExpired() {
        UnauthorizedException ex = new UnauthorizedException("Authentication token has expired");
        ex.addDetail("errorCode", "TOKEN_EXPIRED");
        return ex;
    }

    public static UnauthorizedException tokenInvalid() {
        UnauthorizedException ex = new UnauthorizedException("Invalid authentication token");
        ex.addDetail("errorCode", "TOKEN_INVALID");
        return ex;
    }

    public static UnauthorizedException authenticationRequired() {
        return new UnauthorizedException("Authentication is required to access this resource");
    }
}
