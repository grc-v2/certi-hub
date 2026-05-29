package com.certimaster.shared.exception;

import java.util.Map;

public class InvalidTokenException extends BaseException {

    public InvalidTokenException(String errorCode, String message) {
        super(errorCode, message);
    }

    public InvalidTokenException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public InvalidTokenException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public InvalidTokenException(String errorCode, String message, Throwable cause, Map<String, Object> details) {
        super(errorCode, message, cause, details);
    }
}
