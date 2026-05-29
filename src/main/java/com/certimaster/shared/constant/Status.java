package com.certimaster.shared.constant;

/**
 * Common status enum
 */
public enum Status {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    DELETED("DELETED"),
    ARCHIVED("ARCHIVED");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
