package com.certimaster.identity.domain.event;

import org.springframework.modulith.events.Externalized;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a user requests a password reset.
 * Sent from identity-service.
 */
@Externalized("password-reset-requested::#{getUserId()}")
public class PasswordResetRequestedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String eventId = UUID.randomUUID().toString();
    private Integer eventVersion = 1;
    private Long userId;
    private String email;
    private String resetToken;
    private LocalDateTime expiresAt;
    private LocalDateTime eventTime = LocalDateTime.now();

    public PasswordResetRequestedEvent() {
    }

    public PasswordResetRequestedEvent(Long userId, String email, String resetToken,
                                       LocalDateTime expiresAt, LocalDateTime eventTime) {
        this.userId = userId;
        this.email = email;
        this.resetToken = resetToken;
        this.expiresAt = expiresAt;
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

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
}
