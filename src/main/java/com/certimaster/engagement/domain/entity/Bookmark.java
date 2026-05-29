package com.certimaster.engagement.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "bookmarks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
public class Bookmark extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Bookmark() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private Post post;
        private String notes;

        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder post(Post v) { this.post = v; return this; }
        public Builder notes(String v) { this.notes = v; return this; }

        public Bookmark build() {
            Bookmark o = new Bookmark();
            o.userId = this.userId;
            o.post = this.post;
            o.notes = this.notes;
            return o;
        }
    }
}
