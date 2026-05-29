package com.certimaster.identity.adapter.web.response;

import java.time.LocalDateTime;
import java.util.Set;

public class UserSummaryResponse {

    private Long id;
    private String email;
    private String fullName;
    private String username;
    private String status;
    private Set<String> roleCodes;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;

    public UserSummaryResponse() {
    }

    public UserSummaryResponse(Long id, String email, String fullName, String username,
                               String status, Set<String> roleCodes,
                               LocalDateTime registrationDate, LocalDateTime lastLoginDate) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.status = status;
        this.roleCodes = roleCodes;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(Set<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
