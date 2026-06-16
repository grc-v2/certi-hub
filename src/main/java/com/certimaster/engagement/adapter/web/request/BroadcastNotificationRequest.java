package com.certimaster.engagement.adapter.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BroadcastNotificationRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Message must not be blank")
    private String message;

    @NotBlank(message = "Type must not be blank")
    @Size(max = 40, message = "Type must not exceed 40 characters")
    private String type;

    /** Role code to target (e.g. "ADMIN", "USER"); null/blank = all active users. */
    private String targetRole;

    public BroadcastNotificationRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
}
