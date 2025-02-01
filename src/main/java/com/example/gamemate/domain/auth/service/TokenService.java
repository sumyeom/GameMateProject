package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.LocalLoginResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 블랙리스트 저장
    private final Set<String> blacklist = new ConcurrentHashMap<String, Boolean>().newKeySet();
    private final Map<String, Long> tokenExpirations = new ConcurrentHashMap<>();

    public LocalLoginResponseDto generateLoginTokens(User user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        addRefreshTokenToCookie(response, refreshToken);
        return new LocalLoginResponseDto(accessToken);
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

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true); // 자바 스크립트에서 접근 불가
        cookie.setSecure(true); // HTTPS에서만 동작
        cookie.setPath("/"); // 모든 경로에서 유효
        cookie.setMaxAge(3 * 24 * 60 * 60); // 3일
        response.addCookie(cookie); // 쿠키를 응답에 추가
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void blacklistToken(String token) {
        long expirationTime = jwtTokenProvider.getExpirationFromToken(token);
        blacklist.add(token);
        tokenExpirations.put(token, expirationTime);
        removeExpiredTokens();
    }

    public boolean isBlacklisted(String token) {
        removeExpiredTokens();
        return blacklist.contains(token);
    }

    public boolean validateToken(String token) {
        if (isBlacklisted(token)) {
            return false;
        }
        return jwtTokenProvider.validateToken(token);
    }

    private void removeExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        tokenExpirations.entrySet().removeIf(entry -> {
            if (entry.getValue() < currentTime) {
                blacklist.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

}
