package com.certimaster.identity.application.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);
    private static final String SESSION_KEY_PREFIX = "session:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user_sessions:";

    @Value("${auth.session.ttl-seconds:86400}")
    private long sessionTtlSeconds;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public SessionService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void createSession(Long userId, String sessionToken, String ipAddress, String deviceType,
                              String browser, String operatingSystem, String location) {
        try {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("sessionToken", sessionToken);
            sessionData.put("ipAddress", ipAddress);
            sessionData.put("deviceType", deviceType);
            sessionData.put("browser", browser);
            sessionData.put("operatingSystem", operatingSystem);
            sessionData.put("location", location);
            sessionData.put("loginTimestamp", LocalDateTime.now().toString());

            String sessionKey = SESSION_KEY_PREFIX + userId + ":" + sessionToken;
            String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
            long ttl = effectiveTtl();

            redisTemplate.opsForValue().set(sessionKey, objectMapper.writeValueAsString(sessionData), ttl, TimeUnit.SECONDS);
            redisTemplate.opsForSet().add(userSessionsKey, sessionToken);
            redisTemplate.expire(userSessionsKey, ttl, TimeUnit.SECONDS);

        } catch (JacksonException e) {
            log.error("Failed to serialize session: userId={}", userId, e);
            throw new RuntimeException("Failed to create session", e);
        }
    }

    @Transactional
    public List<Map<String, Object>> getActiveSessions(Long userId) {
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);

        List<Map<String, Object>> sessions = new ArrayList<>();
        if (sessionIds == null || sessionIds.isEmpty()) return sessions;

        for (String sessionId : sessionIds) {
            String sessionKey = SESSION_KEY_PREFIX + userId + ":" + sessionId;
            String json = redisTemplate.opsForValue().get(sessionKey);
            if (json != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = objectMapper.readValue(json, Map.class);
                    sessions.add(data);
                } catch (JacksonException e) {
                    log.error("Failed to deserialize session: userId={}, sessionId={}", userId, sessionId, e);
                }
            } else {
                redisTemplate.opsForSet().remove(userSessionsKey, sessionId);
            }
        }
        return sessions;
    }

    @Transactional
    public void deleteSession(Long userId, String sessionId) {
        redisTemplate.delete(SESSION_KEY_PREFIX + userId + ":" + sessionId);
        redisTemplate.opsForSet().remove(USER_SESSIONS_KEY_PREFIX + userId, sessionId);
    }

    @Transactional
    public int deleteAllUserSessions(Long userId) {
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);
        if (sessionIds == null || sessionIds.isEmpty()) return 0;
        sessionIds.forEach(id -> redisTemplate.delete(SESSION_KEY_PREFIX + userId + ":" + id));
        redisTemplate.delete(userSessionsKey);
        return sessionIds.size();
    }

    @Transactional
    public int forceLogoutAllSessions(Long userId, String logoutReason) {
        return deleteAllUserSessions(userId);
    }

    @Transactional
    public int cleanupDanglingUserSessionReferences(Long userId) {
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);
        if (sessionIds == null || sessionIds.isEmpty()) return 0;

        int removed = 0;
        for (String sessionId : sessionIds) {
            if (Boolean.FALSE.equals(redisTemplate.hasKey(SESSION_KEY_PREFIX + userId + ":" + sessionId))) {
                redisTemplate.opsForSet().remove(userSessionsKey, sessionId);
                removed++;
            }
        }
        return removed;
    }

    private long effectiveTtl() {
        return sessionTtlSeconds > 0 ? sessionTtlSeconds : 86400L;
    }
}
