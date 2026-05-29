package com.certimaster.identity.domain.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis-backed OAuth state store.
 * Key format: oauth:state:{provider}:{state}
 * Value: frontend redirect URI
 * TTL: 10 minutes
 */
@Service
public class OAuthStateService {

    private static final String KEY_PREFIX = "oauth:state:";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;

    public OAuthStateService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generates a cryptographically random state, stores the frontend redirect URI in Redis,
     * and returns the generated state value.
     */
    public String generateAndStore(String provider, String frontendRedirectUri) {
        String state = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(KEY_PREFIX + provider + ":" + state, frontendRedirectUri, TTL);
        return state;
    }

    /**
     * Atomically retrieves and deletes the stored redirect URI (one-time use, multi-instance safe).
     * Returns null if not found or already consumed.
     */
    public String validateAndConsume(String provider, String state) {
        return redisTemplate.opsForValue().getAndDelete(KEY_PREFIX + provider + ":" + state);
    }
}
