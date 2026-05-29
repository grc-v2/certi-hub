package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StartExamRequest {

    @NotBlank(message = "Mode is required")
    @Pattern(regexp = "^(PRACTICE|EXAM)$", message = "Mode must be one of: PRACTICE, EXAM")
    private String mode;

    public StartExamRequest() {
    }

    public StartExamRequest(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String mode;

        public Builder mode(String v) { this.mode = v; return this; }

        public StartExamRequest build() {
            StartExamRequest o = new StartExamRequest();
            o.mode = this.mode;
            return o;
        }
    }
}
