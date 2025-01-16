package com.example.gamemate.global.config.auth;

import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private int refreshTokenMaxAge = 60 * 60 * 24 * 3; //3일

    @Value("${oauth2.success.redirect.uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        log.info("OAuth2 로그인 성공 처리 시작");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        try {
            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            user.updateRefreshToken(refreshToken);
            userRepository.save(user);

            // 쿠키에 Refresh 토큰 저장
            addRefreshTokenCookie(response, refreshToken);

            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", accessToken)
                    .build(false).toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            throw new IOException("OAuth2 인증 처리 중 오류가 발생했습니다.", e);
        }
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenMaxAge);
        response.addCookie(cookie);
    }
}
