package com.example.gamemate.global.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${oauth2.failure.redirect.uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        // 오류 메세지 가져오기
        String errorMessage = exception.getMessage();
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 실패 리다이렉트 URL 생성
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", encodedError)
                .build().toUriString();

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
