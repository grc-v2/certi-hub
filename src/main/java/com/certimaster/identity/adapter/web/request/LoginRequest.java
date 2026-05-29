package com.certimaster.identity.adapter.web.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private Boolean rememberMe = false;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password, Boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private Boolean rememberMe = false;

        public Builder username(String v) { this.username = v; return this; }
        public Builder password(String v) { this.password = v; return this; }
        public Builder rememberMe(Boolean v) { this.rememberMe = v; return this; }

        public LoginRequest build() {
            LoginRequest o = new LoginRequest();
            o.username = this.username;
            o.password = this.password;
            o.rememberMe = this.rememberMe;
            return o;
        }
    }
}
