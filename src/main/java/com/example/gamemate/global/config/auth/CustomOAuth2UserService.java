package com.example.gamemate.global.config.auth;

import com.example.gamemate.domain.auth.dto.OAuth2LoginResponseDto;
import com.example.gamemate.domain.auth.service.AuthService;
import com.example.gamemate.domain.auth.service.OAuth2Service;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2Service oAuth2Service;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            AuthProvider provider = AuthProvider.valueOf(
                    userRequest.getClientRegistration().getRegistrationId().toUpperCase()
            );

            OAuth2LoginResponseDto attributes = oAuth2Service.extractOAuth2Attributes(
                    provider,
                    oauth2User.getAttributes()
            );

            User user = oAuth2Service.registerOAuth2User(attributes);

            return new CustomUserDetails(user, oauth2User.getAttributes());

        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("소셜 로그인 처리 중 오류가 발생했습니다.");
        }
    }
}
