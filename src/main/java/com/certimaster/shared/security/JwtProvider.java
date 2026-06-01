package com.certimaster.shared.security;

import com.certimaster.shared.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private final JwtProperties jwtProperties;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @PostConstruct
    public void init() {
        try {
            if (jwtProperties.getPrivateKey() != null && jwtProperties.getPublicKey() != null
                    && !jwtProperties.getPrivateKey().isEmpty() && !jwtProperties.getPublicKey().isEmpty()) {
                // Load keys from configuration
                this.privateKey = loadPrivateKey(jwtProperties.getPrivateKey());
                this.publicKey = loadPublicKey(jwtProperties.getPublicKey());
            } else {
                // Generate new key pair for development/testing
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                this.privateKey = keyPair.getPrivate();
                this.publicKey = keyPair.getPublic();
                log.warn("JWT Provider initialized with generated RSA keys (development mode). " +
                        "Configure jwt.private-key and jwt.public-key for production.");
            }
        } catch (Exception e) {
            log.error("Failed to initialize JWT Provider", e);
            throw new RuntimeException("Failed to initialize JWT Provider: " + e.getMessage(), e);
        }
    }

    private PrivateKey loadPrivateKey(String base64Key) throws Exception {
        String cleanKey = base64Key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String base64Key) throws Exception {
        String cleanKey = base64Key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public String generateAccessToken(Long userId, String username, String email,
                                       Set<String> roles, Set<String> permissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())   // jti — needed for token blacklist
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("email", email)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("type", "access")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", "refresh")
                .claim("jti", UUID.randomUUID().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_INVALID_SIGNATURE", "Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_MALFORMED", "Invalid JWT token format");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_EXPIRED", "JWT token has expired");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_UNSUPPORTED", "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_EMPTY", "JWT claims string is empty");
        } catch (Exception ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_INVALID", "JWT validation failed: " + ex.getMessage());
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("username", String.class);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Claims claims = validateToken(token);
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? Set.copyOf(roles) : Set.of();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getPermissionsFromToken(String token) {
        Claims claims = validateToken(token);
        List<String> permissions = claims.get("permissions", List.class);
        return permissions != null ? Set.copyOf(permissions) : Set.of();
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (InvalidTokenException e) {
            return true;
        }
    }

    public boolean isAccessToken(String token) {
        Claims claims = validateToken(token);
        return "access".equals(claims.get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        Claims claims = validateToken(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    public String getJtiFromToken(String token) {
        return validateToken(token).getId();
    }
}
