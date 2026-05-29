package com.certimaster.identity.adapter.web.response;

public class BulkOperationError {

    private Long userId;
    private String errorMessage;

    public BulkOperationError() {
    }

    public BulkOperationError(Long userId, String errorMessage) {
        this.userId = userId;
        this.errorMessage = errorMessage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
