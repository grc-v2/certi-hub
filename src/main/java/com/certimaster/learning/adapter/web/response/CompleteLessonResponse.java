package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;

public class CompleteLessonResponse {

    private String message;
    private BigDecimal newProgressPercentage;

    public CompleteLessonResponse() {
    }

    public CompleteLessonResponse(String message, BigDecimal newProgressPercentage) {
        this.message = message;
        this.newProgressPercentage = newProgressPercentage;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public BigDecimal getNewProgressPercentage() { return newProgressPercentage; }
    public void setNewProgressPercentage(BigDecimal newProgressPercentage) { this.newProgressPercentage = newProgressPercentage; }
}
