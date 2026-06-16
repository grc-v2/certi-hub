package com.certimaster.engagement.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * An in-app notification addressed to a single user. Created internally (e.g. from
 * domain events such as exam completion) — there is no public write API for the
 * recipient beyond marking notifications read.
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_user_id", columnList = "user_id"),
    @Index(name = "idx_notifications_user_unread", columnList = "user_id, is_read")
})
public class Notification extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "type", nullable = false, length = 40)
    private String type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "related_entity_id", length = 100)
    private String relatedEntityId;

    public Notification() {
        super();
    }

    public void markRead() {
        this.read = true;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private String type;
        private String title;
        private String message;
        private String relatedEntityId;

        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder message(String v) { this.message = v; return this; }
        public Builder relatedEntityId(String v) { this.relatedEntityId = v; return this; }

        public Notification build() {
            Notification o = new Notification();
            o.userId = this.userId;
            o.type = this.type;
            o.title = this.title;
            o.message = this.message;
            o.relatedEntityId = this.relatedEntityId;
            o.read = false;
            return o;
        }
    }
}
