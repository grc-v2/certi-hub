package com.certimaster.identity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.identity.domain.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 300)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 300)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    private String status = "ACTIVE";

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "last_password_change_date")
    private LocalDateTime lastPasswordChangeDate;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 50)
    private String timezone;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "professional_info", columnDefinition = "jsonb")
    private Map<String, Object> professionalInfo = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "learning_preferences", columnDefinition = "jsonb")
    private Map<String, Object> learningPreferences = new HashMap<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {
        super();
    }

    public User(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                String email, String username, String passwordHash, String fullName, String avatarUrl,
                String phone, String status, Boolean emailVerified, Set<Role> roles) {
        super();
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.status = status;
        this.emailVerified = emailVerified;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public LocalDateTime getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(LocalDateTime lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Map<String, Object> getProfessionalInfo() {
        return professionalInfo;
    }

    public void setProfessionalInfo(Map<String, Object> professionalInfo) {
        this.professionalInfo = professionalInfo;
    }

    public Map<String, Object> getLearningPreferences() {
        return learningPreferences;
    }

    public void setLearningPreferences(Map<String, Object> learningPreferences) {
        this.learningPreferences = learningPreferences;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public Set<String> getAllPermissions() {
        Set<String> permissions = new HashSet<>();
        for (Role role : roles) {
            permissions.addAll(role.getPermissions());
        }
        return permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String username;
        private String passwordHash;
        private String fullName;
        private String avatarUrl;
        private String phone;
        private String status = "ACTIVE";
        private Boolean emailVerified = false;
        private LocalDateTime lastLoginDate;
        private LocalDateTime lastPasswordChangeDate;
        private LocalDateTime deletedAt;
        private Set<Role> roles = new HashSet<>();
        private String bio;
        private String timezone;
        private String preferredLanguage;
        private Map<String, Object> professionalInfo = new HashMap<>();
        private Map<String, Object> learningPreferences = new HashMap<>();

        public Builder email(String v) { this.email = v; return this; }
        public Builder username(String v) { this.username = v; return this; }
        public Builder passwordHash(String v) { this.passwordHash = v; return this; }
        public Builder fullName(String v) { this.fullName = v; return this; }
        public Builder avatarUrl(String v) { this.avatarUrl = v; return this; }
        public Builder phone(String v) { this.phone = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder emailVerified(Boolean v) { this.emailVerified = v; return this; }
        public Builder lastLoginDate(LocalDateTime v) { this.lastLoginDate = v; return this; }
        public Builder lastPasswordChangeDate(LocalDateTime v) { this.lastPasswordChangeDate = v; return this; }
        public Builder deletedAt(LocalDateTime v) { this.deletedAt = v; return this; }
        public Builder roles(Set<Role> v) { this.roles = v; return this; }
        public Builder bio(String v) { this.bio = v; return this; }
        public Builder timezone(String v) { this.timezone = v; return this; }
        public Builder preferredLanguage(String v) { this.preferredLanguage = v; return this; }
        public Builder professionalInfo(Map<String, Object> v) { this.professionalInfo = v; return this; }
        public Builder learningPreferences(Map<String, Object> v) { this.learningPreferences = v; return this; }

        public User build() {
            User o = new User();
            o.email = this.email;
            o.username = this.username;
            o.passwordHash = this.passwordHash;
            o.fullName = this.fullName;
            o.avatarUrl = this.avatarUrl;
            o.phone = this.phone;
            o.status = this.status;
            o.emailVerified = this.emailVerified;
            o.lastLoginDate = this.lastLoginDate;
            o.lastPasswordChangeDate = this.lastPasswordChangeDate;
            o.deletedAt = this.deletedAt;
            o.roles = this.roles;
            o.bio = this.bio;
            o.timezone = this.timezone;
            o.preferredLanguage = this.preferredLanguage;
            o.professionalInfo = this.professionalInfo;
            o.learningPreferences = this.learningPreferences;
            return o;
        }
    }
}
