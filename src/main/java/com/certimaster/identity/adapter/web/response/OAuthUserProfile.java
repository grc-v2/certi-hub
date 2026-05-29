package com.certimaster.identity.adapter.web.response;

public record OAuthUserProfile(String email, String fullName, String avatarUrl, String usernameHint, boolean emailVerified) {
}
