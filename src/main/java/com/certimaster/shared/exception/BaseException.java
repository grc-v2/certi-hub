package com.certimaster.shared.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Base exception class for all custom exceptions
 * Provides common properties and behavior for exception handling
 */
public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, Object> details;

    protected BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = new HashMap<>();
    }

    protected BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = new HashMap<>();
    }

    protected BaseException(String errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details != null ? new HashMap<>(details) : new HashMap<>();
    }

    protected BaseException(String errorCode, String message, Throwable cause, Map<String, Object> details) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details != null ? new HashMap<>(details) : new HashMap<>();
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    /**
     * Add detail to exception
     */
    public BaseException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    /**
     * Add multiple details
     */
    public BaseException addDetails(Map<String, Object> details) {
        if (details != null) {
            this.details.putAll(details);
        }
        return this;
    }

    /**
     * Get detail by key
     */
    public Object getDetail(String key) {
        return this.details.get(key);
    }

    /**
     * Check if detail exists
     */
    public boolean hasDetail(String key) {
        return this.details.containsKey(key);
    }
}
