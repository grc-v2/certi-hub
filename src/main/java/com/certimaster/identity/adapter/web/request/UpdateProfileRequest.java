package com.certimaster.identity.adapter.web.request;

import jakarta.validation.constraints.Size;

import java.util.Map;

public class UpdateProfileRequest {

    @Size(max = 200, message = "Display name must not exceed 200 characters")
    private String displayName;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;

    @Size(max = 10, message = "Preferred language must not exceed 10 characters")
    private String preferredLanguage;

    private Map<String, Object> professionalInfo;

    private Map<String, Object> learningPreferences;

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
}
