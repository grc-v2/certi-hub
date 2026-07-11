package com.certimaster.productivity.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;

public class ResourceResponse extends BaseResponse {

    private Long userId;
    private String kind;
    private String url;
    private String title;
    private String content;
    private Long collectionId;
    private Long certificationId;

    public ResourceResponse() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getCollectionId() { return collectionId; }
    public void setCollectionId(Long collectionId) { this.collectionId = collectionId; }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long userId;
        private String kind;
        private String url;
        private String title;
        private String content;
        private Long collectionId;
        private Long certificationId;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder kind(String v) { this.kind = v; return this; }
        public Builder url(String v) { this.url = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder collectionId(Long v) { this.collectionId = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }

        public ResourceResponse build() {
            ResourceResponse o = new ResourceResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.userId = this.userId;
            o.kind = this.kind;
            o.url = this.url;
            o.title = this.title;
            o.content = this.content;
            o.collectionId = this.collectionId;
            o.certificationId = this.certificationId;
            return o;
        }
    }
}
