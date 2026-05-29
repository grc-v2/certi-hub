package com.certimaster.learning.adapter.web.response;

import java.util.List;

public class BulkImportResponse {

    private int totalRequested;
    private int successCount;
    private int failureCount;
    private List<String> errors;

    public BulkImportResponse() {}

    public BulkImportResponse(int totalRequested, int successCount, int failureCount, List<String> errors) {
        this.totalRequested = totalRequested;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errors = errors;
    }

    public int getTotalRequested() { return totalRequested; }
    public void setTotalRequested(int totalRequested) { this.totalRequested = totalRequested; }

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
