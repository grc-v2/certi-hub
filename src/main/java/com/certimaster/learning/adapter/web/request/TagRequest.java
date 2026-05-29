package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TagRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be one of: ACTIVE, INACTIVE")
    private String status;

    public TagRequest() {
    }

    public TagRequest(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String status;

        public Builder name(String v) { this.name = v; return this; }
        public Builder status(String v) { this.status = v; return this; }

        public TagRequest build() {
            TagRequest o = new TagRequest();
            o.name = this.name;
            o.status = this.status;
            return o;
        }
    }
}
