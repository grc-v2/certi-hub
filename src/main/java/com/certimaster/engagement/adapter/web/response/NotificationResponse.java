package com.certimaster.engagement.adapter.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * In-app notification payload. {@code createdAt} is an ISO-8601 string so the
 * frontend can parse it directly. Shape mirrors the frontend {@code NotificationResponse}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse implements Serializable {

    private Long id;
    private String type;
    private String title;
    private String message;
    private boolean read;
    private String createdAt;
    private String relatedEntityId;

    public NotificationResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }
}
