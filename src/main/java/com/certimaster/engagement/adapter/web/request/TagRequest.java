package com.certimaster.engagement.adapter.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a tag.
 *
 * @see Requirements 4.1
 */
public class TagRequest {

    /**
     * Name of the tag.
     */
    @NotBlank(message = "Tag name is required")
    @Size(max = 100, message = "Tag name must not exceed 100 characters")
    private String name;

    public TagRequest() {
    }

    public TagRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;

        public Builder name(String v) { this.name = v; return this; }

        public TagRequest build() {
            TagRequest o = new TagRequest();
            o.name = this.name;
            return o;
        }
    }
}
