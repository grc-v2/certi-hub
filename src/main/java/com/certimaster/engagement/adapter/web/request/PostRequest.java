package com.certimaster.engagement.adapter.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PostRequest {

    /**
     * Title of the post.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    /**
     * Raw Markdown content of the post.
     */
    @NotBlank(message = "Content is required")
    private String content;

    /**
     * Short excerpt/summary of the post.
     */
    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;

    /**
     * URL of the featured image (from external storage like S3, Cloudinary).
     */
    @Size(max = 500, message = "Featured image URL must not exceed 500 characters")
    private String featuredImage;

    /**
     * List of category IDs to assign to the post.
     */
    private List<Long> categoryIds;

    /**
     * List of tag names to assign to the post.
     * New tags will be created if they don't exist.
     */
    private List<String> tagNames;

    /**
     * SEO title for search engine optimization.
     */
    @Size(max = 255, message = "SEO title must not exceed 255 characters")
    private String seoTitle;

    /**
     * SEO description for search engine optimization.
     */
    @Size(max = 500, message = "SEO description must not exceed 500 characters")
    private String seoDescription;

    @Size(max = 500, message = "SEO keywords must not exceed 500 characters")
    private String seoKeywords;

    private String contentType;
    private String difficultyLevel;

    @Size(max = 500)
    private String subtitle;

    @Size(max = 500)
    private String coverImageUrl;

    @Size(max = 255)
    private String coverImageAlt;

    @Size(max = 500)
    private String ogImageUrl;

    private Long seriesId;

    public PostRequest() {
    }

    public PostRequest(String title, String content, String excerpt, String featuredImage,
                       List<Long> categoryIds, List<String> tagNames,
                       String seoTitle, String seoDescription, String seoKeywords) {
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
        this.featuredImage = featuredImage;
        this.categoryIds = categoryIds;
        this.tagNames = tagNames;
        this.seoTitle = seoTitle;
        this.seoDescription = seoDescription;
        this.seoKeywords = seoKeywords;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getFeaturedImage() { return featuredImage; }
    public void setFeaturedImage(String featuredImage) { this.featuredImage = featuredImage; }

    public List<Long> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }

    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }

    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }

    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }

    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }

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

    public Long getSeriesId() { return seriesId; }
    public void setSeriesId(Long seriesId) { this.seriesId = seriesId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private String content;
        private String excerpt;
        private String featuredImage;
        private List<Long> categoryIds;
        private List<String> tagNames;
        private String seoTitle;
        private String seoDescription;
        private String seoKeywords;

        public Builder title(String v) { this.title = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder excerpt(String v) { this.excerpt = v; return this; }
        public Builder featuredImage(String v) { this.featuredImage = v; return this; }
        public Builder categoryIds(List<Long> v) { this.categoryIds = v; return this; }
        public Builder tagNames(List<String> v) { this.tagNames = v; return this; }
        public Builder seoTitle(String v) { this.seoTitle = v; return this; }
        public Builder seoDescription(String v) { this.seoDescription = v; return this; }
        public Builder seoKeywords(String v) { this.seoKeywords = v; return this; }

        public PostRequest build() {
            PostRequest o = new PostRequest();
            o.title = this.title;
            o.content = this.content;
            o.excerpt = this.excerpt;
            o.featuredImage = this.featuredImage;
            o.categoryIds = this.categoryIds;
            o.tagNames = this.tagNames;
            o.seoTitle = this.seoTitle;
            o.seoDescription = this.seoDescription;
            o.seoKeywords = this.seoKeywords;
            return o;
        }
    }
}
