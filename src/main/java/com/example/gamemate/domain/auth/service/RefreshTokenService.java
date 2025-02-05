package com.example.gamemate.domain.auth.service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final StringRedisTemplate refreshTokenRedisTemplate;
    private final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private int refreshTokenMaxAge = 60 * 60 * 24 * 7; // 7일

    public void saveRefreshToken(String email, String refreshToken, HttpServletResponse response) {
        String key = getKey(email);
        refreshTokenRedisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_TTL);
        addRefreshTokenToCookie(response, refreshToken);
    }

    public String getRefreshToken(String email) {
        String key = getKey(email);
        return refreshTokenRedisTemplate.opsForValue().get(key);
    }

    public void removeRefreshToken(String email, HttpServletResponse response) {
        String key = getKey(email);
        refreshTokenRedisTemplate.delete(key);
        removeRefreshTokenCookie(response);
    }

    private String getKey(String email) {
        return "refresh_token:" + email;
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true); // 자바 스크립트에서 접근 불가
        cookie.setSecure(true); // HTTPS에서만 동작
        cookie.setPath("/"); // 모든 경로에서 유효
        cookie.setMaxAge(refreshTokenMaxAge);
        response.addCookie(cookie); // 쿠키를 응답에 추가
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
