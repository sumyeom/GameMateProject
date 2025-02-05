package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.LoginTokenResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Service
@Transactional
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final StringRedisTemplate tokenBlacklistRedisTemplate;

    public TokenService(
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService,
            @Qualifier("blacklistRedisTemplate") StringRedisTemplate tokenBlacklistRedisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistRedisTemplate = tokenBlacklistRedisTemplate;
    }

    public LoginTokenResponseDto generateLoginTokens(User user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken, response);
        return new LoginTokenResponseDto(accessToken);
    }

    public void blacklistToken(String token) {
        long expirationTime = jwtTokenProvider.getExpirationFromToken(token);
        Duration ttl = Duration.ofMillis(expirationTime - System.currentTimeMillis());
        if (!ttl.isNegative()) {
            tokenBlacklistRedisTemplate.opsForValue().set(getBlacklistKey(token), "1", ttl);
        }
    }

    public boolean validateToken(String token) {
        return !isBlacklisted(token) && jwtTokenProvider.validateToken(token);
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(tokenBlacklistRedisTemplate.hasKey(getBlacklistKey(token)));
    }

    private String getBlacklistKey(String token) {
        return "blacklist:" + token;
    }
}
