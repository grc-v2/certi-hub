package com.certimaster.shared.exception;

import java.util.Map;

/**
 * Exception for business logic violations
 * Use this for expected business rule failures
 */
public class BusinessException extends BaseException {

    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    // Convenience constructors for common scenarios

    public static BusinessException invalidInput(String message) {
        return new BusinessException("INVALID_INPUT", message);
    }

    public static BusinessException operationFailed(String operation) {
        return new BusinessException("OPERATION_FAILED",
                "Failed to perform operation: " + operation);
    }

    public static BusinessException duplicateResource(String resourceType, String identifier) {
        BusinessException ex = new BusinessException("DUPLICATE_RESOURCE",
                String.format("%s already exists", resourceType));
        ex.addDetail("resourceType", resourceType);
        ex.addDetail("identifier", identifier);
        return ex;
    }
}