package com.certimaster.identity.adapter.web.response;

import java.util.ArrayList;
import java.util.List;

public class BulkOperationResult {

    private Integer successCount;
    private Integer failedCount;
    private List<BulkOperationError> errors;

    public BulkOperationResult() {
        this.errors = new ArrayList<>();
    }

    public BulkOperationResult(Integer successCount, Integer failedCount, List<BulkOperationError> errors) {
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public List<BulkOperationError> getErrors() {
        return errors;
    }

    public void setErrors(List<BulkOperationError> errors) {
        this.errors = errors;
    }
}
