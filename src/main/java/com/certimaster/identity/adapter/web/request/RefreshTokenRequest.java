package com.certimaster.identity.adapter.web.request;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    public RefreshTokenRequest() {
    }

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String refreshToken;

        public Builder refreshToken(String v) { this.refreshToken = v; return this; }

        public RefreshTokenRequest build() {
            RefreshTokenRequest o = new RefreshTokenRequest();
            o.refreshToken = this.refreshToken;
            return o;
        }
    }
}
