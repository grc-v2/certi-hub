package com.certimaster.identity.domain.service;

import com.certimaster.shared.security.JwtProperties;
import com.certimaster.shared.exception.UnauthorizedException;
import com.certimaster.identity.domain.entity.RefreshToken;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.adapter.persistence.RefreshTokenRepository;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserWriteRepository userRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserWriteRepository userRepository,
                               JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user, String token) {

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(token)
                .expiresAt(expiresAt)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional
    public User validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        return userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Transactional
    public void revokeUserTokens(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
