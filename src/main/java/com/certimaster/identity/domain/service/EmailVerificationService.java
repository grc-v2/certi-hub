package com.certimaster.identity.domain.service;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.identity.domain.entity.EmailVerificationToken;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.adapter.persistence.EmailVerificationTokenRepository;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserWriteRepository userRepository;

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository,
                                    UserWriteRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createVerificationToken(User user) {

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);
        return token;
    }

    public void sendVerificationEmail(String email, String token) {

        String verificationLink = String.format("http://localhost:8081/api/v1/auth/verify-email?token=%s", token);
        log.info("Verification link for {}: {}", email, verificationLink);
    }

    @Transactional
    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("INVALID_VERIFICATION_TOKEN", "Invalid or expired verification token"));

        if (verificationToken.isVerified()) {
            throw new BusinessException("TOKEN_ALREADY_USED", "This verification token has already been used");
        }

        if (verificationToken.isExpired()) {
            throw new BusinessException("VERIFICATION_TOKEN_EXPIRED", "Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);
    }
}
