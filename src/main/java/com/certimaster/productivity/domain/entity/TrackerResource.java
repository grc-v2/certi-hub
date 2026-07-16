package com.certimaster.productivity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * A single item in a user's Resource Hub — either a text note ({@code kind = NOTE},
 * no URL) or a saved link classified by source (YOUTUBE/GITHUB/BLOG). Merges the
 * old "notes" and "bookmark" concepts into one table.
 */
@Entity
@Table(name = "tracker_resources")
public class TrackerResource extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 16)
    private ResourceKind kind;

    @Column(name = "url", length = 2048)
    private String url;

    @Column(name = "title", length = 512)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** Optional grouping; managed by the collections slice. Plain id column for now. */
    @Column(name = "collection_id")
    private Long collectionId;

    /**
     * Optional link to a learning-module certification the user is studying for.
     * Plain id, no cross-module FK; validated against learning's public lookup API.
     */
    @Column(name = "certification_id")
    private Long certificationId;

    public TrackerResource() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ResourceKind getKind() { return kind; }
    public void setKind(ResourceKind kind) { this.kind = kind; }

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
        private Long userId;
        private ResourceKind kind;
        private String url;
        private String title;
        private String content;
        private Long collectionId;
        private Long certificationId;

        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder kind(ResourceKind v) { this.kind = v; return this; }
        public Builder url(String v) { this.url = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder collectionId(Long v) { this.collectionId = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }

        public TrackerResource build() {
            TrackerResource o = new TrackerResource();
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
