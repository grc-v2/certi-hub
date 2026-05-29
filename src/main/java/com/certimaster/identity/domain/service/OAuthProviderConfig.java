package com.certimaster.identity.domain.service;

public class OAuthProviderConfig {
    private final String provider;
    private final String clientId;
    private final String clientSecret;
    private final String authorizationUrl;
    private final String tokenUrl;
    private final String userInfoUrl;
    private final String scope;

    public OAuthProviderConfig(String provider,
                                String clientId,
                                String clientSecret,
                                String authorizationUrl,
                                String tokenUrl,
                                String userInfoUrl,
                                String scope) {
        this.provider = provider;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationUrl = authorizationUrl;
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
        this.scope = scope;
    }

    public String getProvider() {
        return provider;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public String getScope() {
        return scope;
    }
}
