package com.certimaster.identity.adapter.web.response;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDetailResponse extends UserSummaryResponse {

    private String phone;
    private String avatarUrl;
    private Boolean emailVerified;
    private LocalDateTime lastPasswordChangeDate;
    private Set<String> permissions;

    public UserDetailResponse() {
        super();
    }

    public UserDetailResponse(Long id, String email, String fullName, String username,
                              String status, Set<String> roleCodes,
                              LocalDateTime registrationDate, LocalDateTime lastLoginDate,
                              String phone, String avatarUrl, Boolean emailVerified,
                              LocalDateTime lastPasswordChangeDate, Set<String> permissions) {
        super(id, email, fullName, username, status, roleCodes, registrationDate, lastLoginDate);
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.emailVerified = emailVerified;
        this.lastPasswordChangeDate = lastPasswordChangeDate;
        this.permissions = permissions;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(LocalDateTime lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
