package com.certimaster.engagement.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;

public class TagResponse extends BaseResponse {

    /**
     * Name of the tag.
     */
    private String name;

    /**
     * URL-friendly slug of the tag.
     */
    private String slug;

    /**
     * Number of posts with this tag.
     */
    private Long postCount;

    public TagResponse() {
        super();
    }

    public TagResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                       String createdBy, String updatedBy,
                       String name, String slug, Long postCount) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.name = name;
        this.slug = slug;
        this.postCount = postCount;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Long getPostCount() { return postCount; }
    public void setPostCount(Long postCount) { this.postCount = postCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String name;
        private String slug;
        private Long postCount;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder slug(String v) { this.slug = v; return this; }
        public Builder postCount(Long v) { this.postCount = v; return this; }

        public TagResponse build() {
            TagResponse o = new TagResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.name = this.name;
            o.slug = this.slug;
            o.postCount = this.postCount;
            return o;
        }
    }
}
