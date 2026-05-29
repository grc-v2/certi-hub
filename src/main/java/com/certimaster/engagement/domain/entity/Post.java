package com.certimaster.engagement.domain.entity;

import com.certimaster.engagement.domain.entity.ContentType;
import com.certimaster.engagement.domain.entity.DifficultyLevel;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "content_html", columnDefinition = "TEXT")
    private String contentHtml;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "featured_image", length = 500)
    private String featuredImage;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @Column(name = "reading_time_minutes")
    private Integer readingTimeMinutes;

    // SEO fields
    @Column(name = "seo_title")
    private String seoTitle;

    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;

    @Column(name = "seo_keywords", columnDefinition = "TEXT")
    private String seoKeywords;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", length = 30)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", length = 20)
    private DifficultyLevel difficultyLevel;

    @Column(length = 500)
    private String subtitle;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "cover_image_alt", length = 255)
    private String coverImageAlt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "table_of_contents", columnDefinition = "jsonb")
    private String tableOfContents;

    @Column(name = "word_count")
    private Integer wordCount = 0;

    @Column(name = "unique_view_count")
    private Integer uniqueViewCount = 0;

    @Column(name = "bookmark_count")
    private Integer bookmarkCount = 0;

    @Column(name = "share_count")
    private Integer shareCount = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "co_author_ids", columnDefinition = "jsonb")
    private String coAuthorIds;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "scheduled_publish_at")
    private LocalDateTime scheduledPublishAt;

    @Column(name = "noindex")
    private Boolean noindex = false;

    @Column(name = "pinned")
    private Boolean pinned = false;

    @Column(name = "featured")
    private Boolean featured = false;

    // Flat category/series/cert fields replacing relationships
    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "series_name", length = 200)
    private String seriesName;

    @Column(name = "series_order")
    private Integer seriesOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cert_refs", columnDefinition = "jsonb")
    private List<String> certRefs = new ArrayList<>();

    // Relationships (kept)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PostTagMapping> tagMappings = new HashSet<>();

    public Post() {
        super();
    }

    // Getters and setters
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

    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }

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

    public ContentType getContentType() { return contentType; }
    public void setContentType(ContentType contentType) { this.contentType = contentType; }

    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getCoverImageAlt() { return coverImageAlt; }
    public void setCoverImageAlt(String coverImageAlt) { this.coverImageAlt = coverImageAlt; }

    public String getTableOfContents() { return tableOfContents; }
    public void setTableOfContents(String tableOfContents) { this.tableOfContents = tableOfContents; }

    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }

    public Integer getUniqueViewCount() { return uniqueViewCount; }
    public void setUniqueViewCount(Integer uniqueViewCount) { this.uniqueViewCount = uniqueViewCount; }

    public Integer getBookmarkCount() { return bookmarkCount; }
    public void setBookmarkCount(Integer bookmarkCount) { this.bookmarkCount = bookmarkCount; }

    public Integer getShareCount() { return shareCount; }
    public void setShareCount(Integer shareCount) { this.shareCount = shareCount; }

    public String getCoAuthorIds() { return coAuthorIds; }
    public void setCoAuthorIds(String coAuthorIds) { this.coAuthorIds = coAuthorIds; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public LocalDateTime getScheduledPublishAt() { return scheduledPublishAt; }
    public void setScheduledPublishAt(LocalDateTime scheduledPublishAt) { this.scheduledPublishAt = scheduledPublishAt; }

    public Boolean getNoindex() { return noindex; }
    public void setNoindex(Boolean noindex) { this.noindex = noindex; }

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

    public List<String> getCertRefs() { return certRefs; }
    public void setCertRefs(List<String> certRefs) { this.certRefs = certRefs; }

    public Set<PostTagMapping> getTagMappings() { return tagMappings; }
    public void setTagMappings(Set<PostTagMapping> tagMappings) { this.tagMappings = tagMappings; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private String slug;
        private String content;
        private String contentHtml;
        private String excerpt;
        private String featuredImage;
        private Long authorId;
        private PostStatus status = PostStatus.DRAFT;
        private LocalDateTime publishedAt;
        private Integer viewsCount = 0;
        private Integer likesCount = 0;
        private Integer commentsCount = 0;
        private Integer readingTimeMinutes;
        private String seoTitle;
        private String seoDescription;
        private String seoKeywords;
        private ContentType contentType;
        private DifficultyLevel difficultyLevel;
        private String subtitle;
        private String coverImageUrl;
        private String coverImageAlt;
        private String tableOfContents;
        private Integer wordCount = 0;
        private Boolean pinned = false;
        private Boolean featured = false;

        public Builder title(String v) { this.title = v; return this; }
        public Builder slug(String v) { this.slug = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder contentHtml(String v) { this.contentHtml = v; return this; }
        public Builder excerpt(String v) { this.excerpt = v; return this; }
        public Builder featuredImage(String v) { this.featuredImage = v; return this; }
        public Builder authorId(Long v) { this.authorId = v; return this; }
        public Builder status(PostStatus v) { this.status = v; return this; }
        public Builder publishedAt(LocalDateTime v) { this.publishedAt = v; return this; }
        public Builder viewsCount(Integer v) { this.viewsCount = v; return this; }
        public Builder likesCount(Integer v) { this.likesCount = v; return this; }
        public Builder commentsCount(Integer v) { this.commentsCount = v; return this; }
        public Builder readingTimeMinutes(Integer v) { this.readingTimeMinutes = v; return this; }
        public Builder seoTitle(String v) { this.seoTitle = v; return this; }
        public Builder seoDescription(String v) { this.seoDescription = v; return this; }
        public Builder seoKeywords(String v) { this.seoKeywords = v; return this; }
        public Builder contentType(ContentType v) { this.contentType = v; return this; }
        public Builder difficultyLevel(DifficultyLevel v) { this.difficultyLevel = v; return this; }
        public Builder subtitle(String v) { this.subtitle = v; return this; }
        public Builder coverImageUrl(String v) { this.coverImageUrl = v; return this; }
        public Builder coverImageAlt(String v) { this.coverImageAlt = v; return this; }
        public Builder tableOfContents(String v) { this.tableOfContents = v; return this; }
        public Builder wordCount(Integer v) { this.wordCount = v; return this; }
        public Builder pinned(Boolean v) { this.pinned = v; return this; }
        public Builder featured(Boolean v) { this.featured = v; return this; }

        public Post build() {
            Post o = new Post();
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
            o.contentType = this.contentType;
            o.difficultyLevel = this.difficultyLevel;
            o.subtitle = this.subtitle;
            o.coverImageUrl = this.coverImageUrl;
            o.coverImageAlt = this.coverImageAlt;
            o.tableOfContents = this.tableOfContents;
            o.wordCount = this.wordCount;
            o.pinned = this.pinned;
            o.featured = this.featured;
            return o;
        }
    }
}
