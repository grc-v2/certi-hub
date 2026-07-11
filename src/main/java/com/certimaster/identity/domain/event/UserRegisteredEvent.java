package com.certimaster.identity.domain.event;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a new user successfully registers.
 * Sent from identity-service. Consumed by notification-service to deliver
 * a welcome email / in-app notification.
 */
public class UserRegisteredEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String eventId = UUID.randomUUID().toString();
    private Integer eventVersion = 1;
    private Long userId;
    private String email;
    private String username;
    private String fullName;
    private LocalDateTime eventTime = LocalDateTime.now();

    public UserRegisteredEvent() {
    }

    public UserRegisteredEvent(Long userId, String email, String username, String fullName, LocalDateTime eventTime) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.eventTime = eventTime;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Integer getEventVersion() { return eventVersion; }
    public void setEventVersion(Integer eventVersion) { this.eventVersion = eventVersion; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
}
