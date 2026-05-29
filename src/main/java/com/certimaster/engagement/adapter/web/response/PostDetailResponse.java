package com.certimaster.engagement.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;
import com.certimaster.engagement.adapter.web.response.TagResponse;

import java.time.LocalDateTime;
import java.util.List;

public class PostDetailResponse extends BaseResponse {

    private String title;
    private String slug;
    private String content;
    private String contentHtml;
    private String excerpt;
    private String featuredImage;
    private Long authorId;
    private String status;
    private LocalDateTime publishedAt;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer readingTimeMinutes;
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
    private List<TagResponse> tags;
    private ReactionResponse currentUserReaction;
    private String contentType;
    private String difficultyLevel;
    private String subtitle;
    private String coverImageUrl;
    private String coverImageAlt;
    private String ogImageUrl;
    private String tableOfContents;
    private Integer wordCount;
    private Integer bookmarkCount;
    private Boolean pinned;
    private Boolean featured;
    private String category;
    private String seriesName;
    private Integer seriesOrder;

    public PostDetailResponse() {
        super();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }

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

    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }

    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }

    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }

    public List<TagResponse> getTags() { return tags; }
    public void setTags(List<TagResponse> tags) { this.tags = tags; }

    public ReactionResponse getCurrentUserReaction() { return currentUserReaction; }
    public void setCurrentUserReaction(ReactionResponse currentUserReaction) { this.currentUserReaction = currentUserReaction; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getCoverImageAlt() { return coverImageAlt; }
    public void setCoverImageAlt(String coverImageAlt) { this.coverImageAlt = coverImageAlt; }

    public String getOgImageUrl() { return ogImageUrl; }
    public void setOgImageUrl(String ogImageUrl) { this.ogImageUrl = ogImageUrl; }

    public String getTableOfContents() { return tableOfContents; }
    public void setTableOfContents(String tableOfContents) { this.tableOfContents = tableOfContents; }

    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }

    public Integer getBookmarkCount() { return bookmarkCount; }
    public void setBookmarkCount(Integer bookmarkCount) { this.bookmarkCount = bookmarkCount; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }

    public Integer getSeriesOrder() { return seriesOrder; }
    public void setSeriesOrder(Integer seriesOrder) { this.seriesOrder = seriesOrder; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String title;
        private String slug;
        private String content;
        private String contentHtml;
        private String excerpt;
        private String featuredImage;
        private Long authorId;
        private String status;
        private LocalDateTime publishedAt;
        private Integer viewsCount;
        private Integer likesCount;
        private Integer commentsCount;
        private Integer readingTimeMinutes;
        private String seoTitle;
        private String seoDescription;
        private String seoKeywords;
        private List<TagResponse> tags;
        private ReactionResponse currentUserReaction;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder slug(String v) { this.slug = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder contentHtml(String v) { this.contentHtml = v; return this; }
        public Builder excerpt(String v) { this.excerpt = v; return this; }
        public Builder featuredImage(String v) { this.featuredImage = v; return this; }
        public Builder authorId(Long v) { this.authorId = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder publishedAt(LocalDateTime v) { this.publishedAt = v; return this; }
        public Builder viewsCount(Integer v) { this.viewsCount = v; return this; }
        public Builder likesCount(Integer v) { this.likesCount = v; return this; }
        public Builder commentsCount(Integer v) { this.commentsCount = v; return this; }
        public Builder readingTimeMinutes(Integer v) { this.readingTimeMinutes = v; return this; }
        public Builder seoTitle(String v) { this.seoTitle = v; return this; }
        public Builder seoDescription(String v) { this.seoDescription = v; return this; }
        public Builder seoKeywords(String v) { this.seoKeywords = v; return this; }
        public Builder tags(List<TagResponse> v) { this.tags = v; return this; }
        public Builder currentUserReaction(ReactionResponse v) { this.currentUserReaction = v; return this; }

        public PostDetailResponse build() {
            PostDetailResponse o = new PostDetailResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.title = this.title;
            o.slug = this.slug;
            o.content = this.content;
            o.contentHtml = this.contentHtml;
            o.excerpt = this.excerpt;
            o.featuredImage = this.featuredImage;
            o.authorId = this.authorId;
            o.status = this.status;
            o.publishedAt = this.publishedAt;
            o.viewsCount = this.viewsCount;
            o.likesCount = this.likesCount;
            o.commentsCount = this.commentsCount;
            o.readingTimeMinutes = this.readingTimeMinutes;
            o.seoTitle = this.seoTitle;
            o.seoDescription = this.seoDescription;
            o.seoKeywords = this.seoKeywords;
            o.tags = this.tags;
            o.currentUserReaction = this.currentUserReaction;
            return o;
        }
    }
}
