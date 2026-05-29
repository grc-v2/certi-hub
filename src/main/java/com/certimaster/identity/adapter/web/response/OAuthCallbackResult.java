package com.certimaster.identity.adapter.web.response;

public record OAuthCallbackResult(LoginResponse loginResponse, String frontendRedirectUri) {
}
