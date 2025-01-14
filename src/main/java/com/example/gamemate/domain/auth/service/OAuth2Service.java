package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.OAuth2LoginResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.AuthProvider;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2Service {

    private final UserRepository userRepository;

    public OAuth2LoginResponseDto extractOAuth2Attributes(AuthProvider provider, Map<String, Object> attributes) {
        if(provider == AuthProvider.GOOGLE) {
            return extractGoogleAttributes(attributes);
        } else if(provider == AuthProvider.KAKAO) {
            return extractKakaoAttributes(attributes);
        }
        throw new ApiException(ErrorCode.INVALID_PROVIDER_TYPE);
    }

    public User registerOAuth2User(OAuth2LoginResponseDto responseDto) {
        User findUser = userRepository.findByEmail(responseDto.getEmail())
                .orElseGet(() -> {
                    User newUser = new User(
                            responseDto.getEmail(),
                            responseDto.getName(),
                            responseDto.getName(),
                            responseDto.getProvider(),
                            responseDto.getProviderId()
                    );
                    return userRepository.save(newUser);
                });

        if (findUser.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }
        if (!findUser.getProvider().equals(responseDto.getProvider())) {
            throw new ApiException(ErrorCode.INVALID_PROVIDER_TYPE);
        }
        return findUser;
    }

    private OAuth2LoginResponseDto extractGoogleAttributes(Map<String, Object> attributes) {
        return new OAuth2LoginResponseDto(
                getSafeString(attributes.get("sub")),
                getSafeString(attributes.get("email")),
                getSafeString(attributes.get("name")),
                AuthProvider.GOOGLE
        );
    }

    private OAuth2LoginResponseDto extractKakaoAttributes(Map<String, Object> attributes) {
        String providerId = getSafeString(attributes.get("id"));

        Map<String, Object> kakaoAccount = getSafeMap(attributes, "kakao_account");
        Map<String, Object> profile = getSafeMap(kakaoAccount, "profile");

        return new OAuth2LoginResponseDto(
                providerId,
                getSafeString(kakaoAccount.get("email")),
                getSafeString(profile.get("nickname")),
                AuthProvider.KAKAO
        );
    }

    private String getSafeString(Object obj) {
        if (obj == null) {
            throw new ApiException(ErrorCode.INVALID_OAUTH2_ATTRIBUTE);
        }
        return obj.toString();
    }

    private Map<String, Object> getSafeMap(Map<String, Object> attributes, String attributeName) {
        Object attributeValue = attributes.get(attributeName);
        // instanceof 검사를 통해 타입 안정성 확보, null 체크 포함
        if (!(attributeValue instanceof Map)) {
            throw new ApiException(ErrorCode.INVALID_OAUTH2_ATTRIBUTE);
        }
        return (Map<String, Object>) attributeValue;
    }

}
