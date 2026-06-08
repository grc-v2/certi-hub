package com.certimaster.identity.application.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    private final ObjectMapper objectMapper = new JsonMapper();

    private SessionService service;

    @BeforeEach
    void setUp() {
        service = new SessionService(redisTemplate, objectMapper);
    }

    @Test
    void createSessionSerializesSessionDataWithJackson3Mapper() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        service.createSession(42L, "token-1", "127.0.0.1", "desktop", "Firefox", "macOS", "Hanoi");

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq("session:42:token-1"), jsonCaptor.capture(), eq(86400L), eq(TimeUnit.SECONDS));
        verify(setOperations).add("user_sessions:42", "token-1");
        verify(redisTemplate).expire("user_sessions:42", 86400L, TimeUnit.SECONDS);

        @SuppressWarnings("unchecked")
        Map<String, Object> sessionData = objectMapper.readValue(jsonCaptor.getValue(), Map.class);
        assertThat(sessionData)
                .containsEntry("sessionToken", "token-1")
                .containsEntry("ipAddress", "127.0.0.1")
                .containsEntry("deviceType", "desktop")
                .containsEntry("browser", "Firefox")
                .containsEntry("operatingSystem", "macOS")
                .containsEntry("location", "Hanoi");
        assertThat(sessionData.get("loginTimestamp")).isInstanceOf(String.class);
    }

    @Test
    void getActiveSessionsDeserializesSessionJsonWithJackson3Mapper() {
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("sessionToken", "token-1");
        sessionData.put("ipAddress", "127.0.0.1");
        sessionData.put("deviceType", "desktop");
        sessionData.put("browser", "Firefox");
        sessionData.put("operatingSystem", "macOS");
        sessionData.put("location", "Hanoi");
        sessionData.put("loginTimestamp", "2026-06-03T08:00:00");

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(setOperations.members("user_sessions:42")).thenReturn(Set.of("token-1"));
        when(valueOperations.get("session:42:token-1")).thenReturn(objectMapper.writeValueAsString(sessionData));

        List<Map<String, Object>> sessions = service.getActiveSessions(42L);

        assertThat(sessions).singleElement().satisfies(session -> assertThat(session)
                .containsEntry("sessionToken", "token-1")
                .containsEntry("browser", "Firefox")
                .containsEntry("loginTimestamp", "2026-06-03T08:00:00"));
    }
}
