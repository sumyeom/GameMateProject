package com.example.gamemate.global.config.auth;

import com.example.gamemate.domain.auth.dto.LoginTokenResponseDto;
import com.example.gamemate.domain.auth.service.TokenService;
import com.example.gamemate.domain.user.entity.User;
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

    private final TokenService tokenService;

    @Value("${oauth2.success.redirect-uri}")
    private String successRedirectUri;

    @Value("${oauth2.set-password.redirect-uri}")
    private String passwordSetupRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        log.info("OAuth2 로그인 성공 처리 시작");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        try {
            // 토큰 생성 및 저장
            LoginTokenResponseDto tokenDto = tokenService.generateLoginTokens(user, response);

            String targetUrl;
            if("OAUTH2_USER".equals(user.getPassword())) {
                targetUrl = UriComponentsBuilder.fromUriString(passwordSetupRedirectUri)
                        .queryParam("token", tokenDto.getAccessToken())
                        .build(false).toUriString();
            } else {
                targetUrl = UriComponentsBuilder.fromUriString(successRedirectUri)
                        .queryParam("token", tokenDto.getAccessToken())
                        .build(false).toUriString();
            }

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            throw new IOException("OAuth2 인증 처리 중 오류가 발생했습니다.", e);
        }
    }
}
