package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Min;

public class PauseSessionRequest {

    @Min(value = 0, message = "Time spent must be non-negative")
    private Integer timeSpentSeconds;

    public PauseSessionRequest() {
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }
}
