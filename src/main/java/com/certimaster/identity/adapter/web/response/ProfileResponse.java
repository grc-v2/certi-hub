package com.certimaster.identity.adapter.web.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ProfileResponse {

    private Long userId;
    private String email;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String displayName;
    private String bio;
    private String timezone;
    private String preferredLanguage;
    private Map<String, Object> professionalInfo;
    private Map<String, Object> learningPreferences;
    private int completenessScore;
    private LocalDateTime updatedAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public int getCompletenessScore() {
        return completenessScore;
    }

    public void setCompletenessScore(int completenessScore) {
        this.completenessScore = completenessScore;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
