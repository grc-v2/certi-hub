package com.certimaster.engagement.adapter.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {

    /** Parent comment id for a reply; null for a top-level comment. */
    private Long parentId;

    @NotBlank(message = "Comment content must not be blank")
    @Size(max = 5000, message = "Comment content must not exceed 5000 characters")
    private String content;

    public CommentRequest() {}

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
