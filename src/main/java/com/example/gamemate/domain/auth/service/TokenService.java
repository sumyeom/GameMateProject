package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.EmailLoginResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public EmailLoginResponseDto generateLoginTokens(User user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        addRefreshTokenToCookie(response, refreshToken);
        return new EmailLoginResponseDto(accessToken);
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

}
