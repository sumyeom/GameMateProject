package com.example.gamemate.global.config.auth;

import com.example.gamemate.domain.auth.service.AuthService;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.domain.user.service.UserService;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;  // UserRepository 대신 AuthService 사용
    private final UserRepository userRepository;
    private int refreshTokenMaxAge = 1000 * 60 * 60 * 24 * 3; //3일

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        try {
            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            // AuthService에 토큰 저장 처리 위임
            user.updateRefreshToken(refreshToken);
            userRepository.save(user);

            // 쿠키에 Refresh 토큰 저장
            addRefreshTokenCookie(response, refreshToken);

            // Access 토큰과 함께 리다이렉트
            getRedirectStrategy().sendRedirect(
                    request,
                    response,
                    determineTargetUrl(accessToken)
            );
        } catch (Exception e) {
            throw new IOException("OAuth2 인증 처리 중 오류가 발생했습니다.", e);
        }
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenMaxAge);
        response.addCookie(cookie);
    }

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    private String determineTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();
    }
}
