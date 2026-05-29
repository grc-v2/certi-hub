package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;

public class TagResponse extends BaseResponse {

    private String name;
    private String status;

    public TagResponse() {
    }

    public TagResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                       String createdBy, String updatedBy,
                       String name, String status) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
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
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String name;
        private String status;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder status(String v) { this.status = v; return this; }

        public TagResponse build() {
            TagResponse o = new TagResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.name = this.name;
            o.status = this.status;
            return o;
        }
    }
}
