package com.certimaster.identity.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.domain.event.PasswordResetRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final String PREFIX = "password-reset:";

    private final StringRedisTemplate redisTemplate;
    private final UserWriteRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEvents;
    private final Duration ttl;

    public PasswordResetService(
            StringRedisTemplate redisTemplate,
            UserWriteRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher applicationEvents,
            @Value("${auth.password-reset.token-expiration-minutes:30}") long expirationMinutes) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.applicationEvents = applicationEvents;
        this.ttl = Duration.ofMinutes(expirationMinutes);
    }

    public void requestReset(RequestPasswordResetCommand command) {
        Optional<User> userOpt = userRepository.findByEmailWithRoles(command.email());
        if (userOpt.isEmpty()) {
            log.info("Password reset requested for unknown email (no-op): {}", command.email());
            return;
        }
        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(PREFIX + token, user.getId().toString(), ttl);

        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent();
        event.setUserId(user.getId());
        event.setEmail(user.getEmail());
        event.setResetToken(token);
        event.setExpiresAt(LocalDateTime.now().plus(ttl));
        applicationEvents.publishEvent(event);

        log.info("Password reset token issued for userId={}", user.getId());
    }

    @Transactional
    public void resetPassword(ResetPasswordCommand command) {
        String key = PREFIX + command.token();
        String userIdStr = redisTemplate.opsForValue().get(key);
        if (userIdStr == null) {
            throw new BusinessException("INVALID_RESET_TOKEN", "Invalid or expired reset token");
        }
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            log.error("Corrupt password reset token value: {}", userIdStr);
            redisTemplate.delete(key);
            throw new BusinessException("INVALID_RESET_TOKEN", "Invalid or expired reset token");
        }

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
        user.setPasswordHash(passwordEncoder.encode(command.newPassword()));
        userRepository.save(user);

        redisTemplate.delete(key);
        log.info("Password reset completed for userId={}", userId);
    }
}
