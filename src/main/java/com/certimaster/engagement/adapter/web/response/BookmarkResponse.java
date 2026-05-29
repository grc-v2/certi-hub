package com.certimaster.engagement.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;
import com.certimaster.engagement.adapter.web.response.TagResponse;

import java.time.LocalDateTime;
import java.util.List;

public class BookmarkResponse extends BaseResponse {

    // Bookmark-specific fields
    private Long userId;
    private Long collectionId;
    private String notes;

    // Post fields
    private String title;
    private String slug;
    private String excerpt;
    private String featuredImage;
    private Long authorId;
    private String status;
    private LocalDateTime publishedAt;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer readingTimeMinutes;
    private List<TagResponse> tags;
    private String contentType;
    private String difficultyLevel;
    private String subtitle;
    private String coverImageUrl;
    private Boolean pinned;
    private Boolean featured;
    private String category;

    public BookmarkResponse() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCollectionId() { return collectionId; }
    public void setCollectionId(Long collectionId) { this.collectionId = collectionId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getFeaturedImage() { return featuredImage; }
    public void setFeaturedImage(String featuredImage) { this.featuredImage = featuredImage; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public Integer getViewsCount() { return viewsCount; }
    public void setViewsCount(Integer viewsCount) { this.viewsCount = viewsCount; }

    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }

    public Integer getReadingTimeMinutes() { return readingTimeMinutes; }
    public void setReadingTimeMinutes(Integer readingTimeMinutes) { this.readingTimeMinutes = readingTimeMinutes; }

    public List<TagResponse> getTags() { return tags; }
    public void setTags(List<TagResponse> tags) { this.tags = tags; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long userId;
        private Long collectionId;
        private String notes;
        private String title;
        private String slug;
        private String excerpt;
        private String featuredImage;
        private Long authorId;
        private String status;
        private LocalDateTime publishedAt;
        private Integer viewsCount;
        private Integer likesCount;
        private Integer commentsCount;
        private Integer readingTimeMinutes;
        private List<TagResponse> tags;
        private String contentType;
        private String difficultyLevel;
        private String subtitle;
        private String coverImageUrl;
        private Boolean pinned;
        private Boolean featured;
        private String category;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder collectionId(Long v) { this.collectionId = v; return this; }
        public Builder notes(String v) { this.notes = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder slug(String v) { this.slug = v; return this; }
        public Builder excerpt(String v) { this.excerpt = v; return this; }
        public Builder featuredImage(String v) { this.featuredImage = v; return this; }
        public Builder authorId(Long v) { this.authorId = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder publishedAt(LocalDateTime v) { this.publishedAt = v; return this; }
        public Builder viewsCount(Integer v) { this.viewsCount = v; return this; }
        public Builder likesCount(Integer v) { this.likesCount = v; return this; }
        public Builder commentsCount(Integer v) { this.commentsCount = v; return this; }
        public Builder readingTimeMinutes(Integer v) { this.readingTimeMinutes = v; return this; }
        public Builder tags(List<TagResponse> v) { this.tags = v; return this; }
        public Builder contentType(String v) { this.contentType = v; return this; }
        public Builder difficultyLevel(String v) { this.difficultyLevel = v; return this; }
        public Builder subtitle(String v) { this.subtitle = v; return this; }
        public Builder coverImageUrl(String v) { this.coverImageUrl = v; return this; }
        public Builder pinned(Boolean v) { this.pinned = v; return this; }
        public Builder featured(Boolean v) { this.featured = v; return this; }
        public Builder category(String v) { this.category = v; return this; }

        public BookmarkResponse build() {
            BookmarkResponse o = new BookmarkResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.userId = this.userId;
            o.collectionId = this.collectionId;
            o.notes = this.notes;
            o.title = this.title;
            o.slug = this.slug;
            o.excerpt = this.excerpt;
            o.featuredImage = this.featuredImage;
            o.authorId = this.authorId;
            o.status = this.status;
            o.publishedAt = this.publishedAt;
            o.viewsCount = this.viewsCount;
            o.likesCount = this.likesCount;
            o.commentsCount = this.commentsCount;
            o.readingTimeMinutes = this.readingTimeMinutes;
            o.tags = this.tags;
            o.contentType = this.contentType;
            o.difficultyLevel = this.difficultyLevel;
            o.subtitle = this.subtitle;
            o.coverImageUrl = this.coverImageUrl;
            o.pinned = this.pinned;
            o.featured = this.featured;
            o.category = this.category;
            return o;
        }
    }
}
