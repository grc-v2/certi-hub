package com.certimaster.engagement.adapter.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * Threaded comment payload. {@code createdAt} is an ISO-8601 string so the
 * frontend can parse it directly with {@code new Date(...)}. Shape mirrors the
 * frontend {@code CommentResponse}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse implements Serializable {

    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String createdAt;
    private List<CommentResponse> replies;

    public CommentResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<CommentResponse> getReplies() { return replies; }
    public void setReplies(List<CommentResponse> replies) { this.replies = replies; }
}
