package com.certimaster.shared.exception;

import java.util.Map;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String message, Map<String, Object> details) {
        super("RESOURCE_NOT_FOUND", message, details);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super("RESOURCE_NOT_FOUND", message, cause);
    }

    // Convenience factory methods

    public static ResourceNotFoundException byId(String resourceType, Long id) {
        ResourceNotFoundException ex = new ResourceNotFoundException(
                String.format("%s not found with id: %d", resourceType, id));
        ex.addDetail("resourceType", resourceType);
        ex.addDetail("id", id);
        return ex;
    }

    public static ResourceNotFoundException byField(String resourceType, String fieldName, Object fieldValue) {
        ResourceNotFoundException ex = new ResourceNotFoundException(
                String.format("%s not found with %s: %s", resourceType, fieldName, fieldValue));
        ex.addDetail("resourceType", resourceType);
        ex.addDetail("fieldName", fieldName);
        ex.addDetail("fieldValue", fieldValue);
        return ex;
    }

    public static ResourceNotFoundException custom(String resourceType, String criteria) {
        ResourceNotFoundException ex = new ResourceNotFoundException(
                String.format("%s not found: %s", resourceType, criteria));
        ex.addDetail("resourceType", resourceType);
        ex.addDetail("criteria", criteria);
        return ex;
    }
}
