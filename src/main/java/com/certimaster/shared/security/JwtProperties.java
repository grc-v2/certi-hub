package com.certimaster.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties for RS256 algorithm
 * Supports RSA asymmetric key pairs for token signing and verification
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Base64 encoded RSA private key for signing tokens
     */
    private String privateKey;

    /**
     * Base64 encoded RSA public key for verifying tokens
     */
    private String publicKey;

    /**
     * Access token expiration time in milliseconds (default: 15 minutes)
     */
    private long accessTokenExpiration = 900000L;

    /**
     * Refresh token expiration time in milliseconds (default: 7 days)
     */
    private long refreshTokenExpiration = 604800000L;

    /**
     * JWT issuer claim (default: "certimaster-auth")
     */
    private String issuer = "certimaster-auth";

    /**
     * When true and no keypair is configured, generate an ephemeral RSA keypair
     * at startup (development only — tokens are invalidated on every restart).
     * When false (default), the application fails fast if keys are missing.
     */
    private boolean generateDevKeys = false;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public boolean isGenerateDevKeys() {
        return generateDevKeys;
    }

    public void setGenerateDevKeys(boolean generateDevKeys) {
        this.generateDevKeys = generateDevKeys;
    }
}
