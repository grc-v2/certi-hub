package com.certimaster.identity.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.domain.event.PasswordResetRequestedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private UserWriteRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher applicationEvents;

    private PasswordResetService service;

    @BeforeEach
    void setUp() {
        service = new PasswordResetService(
                redisTemplate,
                userRepository,
                passwordEncoder,
                applicationEvents,
                30
        );
    }

    @Test
    void requestResetNoOpsWhenEmailDoesNotExist() {
        when(userRepository.findByEmailWithRoles("missing@example.com")).thenReturn(Optional.empty());

        service.requestReset(new RequestPasswordResetCommand("missing@example.com"));

        verify(redisTemplate, never()).opsForValue();
        verify(applicationEvents, never()).publishEvent(any());
    }

    @Test
    void requestResetStoresTokenWithTtlAndPublishesResetEvent() {
        User user = activeUser();
        when(userRepository.findByEmailWithRoles("learner@example.com")).thenReturn(Optional.of(user));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service.requestReset(new RequestPasswordResetCommand("learner@example.com"));

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(keyCaptor.capture(), eq("42"), eq(Duration.ofMinutes(30)));
        assertThat(keyCaptor.getValue()).startsWith("password-reset:");

        ArgumentCaptor<PasswordResetRequestedEvent> eventCaptor =
                ArgumentCaptor.forClass(PasswordResetRequestedEvent.class);
        verify(applicationEvents).publishEvent(eventCaptor.capture());
        PasswordResetRequestedEvent event = eventCaptor.getValue();
        assertThat(event.getUserId()).isEqualTo(42L);
        assertThat(event.getEmail()).isEqualTo("learner@example.com");
        assertThat(event.getResetToken()).isEqualTo(keyCaptor.getValue().substring("password-reset:".length()));
        assertThat(event.getEventVersion()).isEqualTo(1);
        assertThat(event.getEventId()).isNotBlank();
    }

    @Test
    void resetPasswordRejectsMissingOrExpiredToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("password-reset:missing-token")).thenReturn(null);

        assertThatThrownBy(() -> service.resetPassword(new ResetPasswordCommand("missing-token", "new-secret")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid or expired reset token");

        verify(userRepository, never()).save(any(User.class));
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void resetPasswordDeletesCorruptTokenValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("password-reset:bad-token")).thenReturn("not-a-user-id");

        assertThatThrownBy(() -> service.resetPassword(new ResetPasswordCommand("bad-token", "new-secret")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid or expired reset token");

        verify(redisTemplate).delete("password-reset:bad-token");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPasswordUpdatesPasswordHashAndConsumesToken() {
        User user = activeUser();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("password-reset:valid-token")).thenReturn("42");
        when(userRepository.findByIdWithRoles(42L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-secret")).thenReturn("new-hash");

        service.resetPassword(new ResetPasswordCommand("valid-token", "new-secret"));

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        verify(userRepository).save(user);
        verify(redisTemplate).delete("password-reset:valid-token");
    }

    private User activeUser() {
        User user = User.builder()
                .email("learner@example.com")
                .username("learner")
                .passwordHash("old-hash")
                .fullName("Learner User")
                .status("ACTIVE")
                .build();
        user.setId(42L);
        return user;
    }
}
