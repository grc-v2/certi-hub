package com.certimaster.identity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.identity.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    public EmailVerificationToken() {
        super();
    }

    public EmailVerificationToken(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                                  User user, String token, LocalDateTime expiresAt, LocalDateTime verifiedAt) {
        super();
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.verifiedAt = verifiedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private String token;
        private LocalDateTime expiresAt;
        private LocalDateTime verifiedAt;

        public Builder user(User v) { this.user = v; return this; }
        public Builder token(String v) { this.token = v; return this; }
        public Builder expiresAt(LocalDateTime v) { this.expiresAt = v; return this; }
        public Builder verifiedAt(LocalDateTime v) { this.verifiedAt = v; return this; }

        public EmailVerificationToken build() {
            EmailVerificationToken o = new EmailVerificationToken();
            o.user = this.user;
            o.token = this.token;
            o.expiresAt = this.expiresAt;
            o.verifiedAt = this.verifiedAt;
            return o;
        }
    }
}
