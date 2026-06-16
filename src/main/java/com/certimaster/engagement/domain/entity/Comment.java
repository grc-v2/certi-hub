package com.certimaster.engagement.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * A comment on a blog {@link Post}. Threading is modelled with a self-referential
 * {@code parentId} (null for top-level comments). The author's display name is
 * denormalised at write time from the authenticated principal — the engagement
 * module is not allowed to depend on identity (see package-info), and the blog
 * already treats the author as an opaque id.
 */
@Entity
@Table(name = "post_comments", indexes = {
    @Index(name = "idx_post_comments_post_id", columnList = "post_id"),
    @Index(name = "idx_post_comments_parent_id", columnList = "parent_id")
})
public class Comment extends BaseEntity {

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "author_name", length = 100)
    private String authorName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public Comment() {
        super();
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long postId;
        private Long parentId;
        private Long authorId;
        private String authorName;
        private String content;

        public Builder postId(Long v) { this.postId = v; return this; }
        public Builder parentId(Long v) { this.parentId = v; return this; }
        public Builder authorId(Long v) { this.authorId = v; return this; }
        public Builder authorName(String v) { this.authorName = v; return this; }
        public Builder content(String v) { this.content = v; return this; }

        public Comment build() {
            Comment o = new Comment();
            o.postId = this.postId;
            o.parentId = this.parentId;
            o.authorId = this.authorId;
            o.authorName = this.authorName;
            o.content = this.content;
            return o;
        }
    }
}
