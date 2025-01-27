package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.OAuth2LoginResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.AuthProvider;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
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

    public User processOAuth2User(OAuth2LoginResponseDto responseDto) {
        // 기존 사용자 조회
        Optional<User> findUser = userRepository.findByEmail(responseDto.getEmail());

        // 기존 사용자 존재하는 경우
        if (findUser.isPresent()) {
            User existingUser = findUser.get();

            // 탈퇴한 사용자 체크
            if (existingUser.getUserStatus() == UserStatus.WITHDRAW) {
                throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
            }

            // 다른 OAuth 제공자로 로그인 시도한 경우
            if (!existingUser.getProvider().equals(responseDto.getProvider())) {
                throw new ApiException(ErrorCode.INVALID_PROVIDER_TYPE);
            }

            return existingUser;
        }

        // 새로운 사용자 생성
        User newUser = new User(
                responseDto.getEmail(),
                responseDto.getName(),
                responseDto.getName(),
                responseDto.getProvider(),
                responseDto.getProviderId()
        );
        return userRepository.save(newUser);
    }

//    public String generateAuthorizationUrl(String providerName, String redirectUri) {
//
//        AuthProvider provider = AuthProvider.fromString(providerName);
//        ClientRegistration clientRegistration = getClientRegistration(provider);
//        String state = UUID.randomUUID().toString();
//        log.info("Generated OAuth2 State for provider {}: {}", providerName, state);
//
//        String authorizationUrl = UriComponentsBuilder
//                .fromUriString(clientRegistration.getProviderDetails().getAuthorizationUri())
//                .queryParam("client_id", clientRegistration.getClientId())
//                .queryParam("redirect_uri", redirectUri != null ? redirectUri : clientRegistration.getRedirectUri())
//                .queryParam("response_type", "code")
//                .queryParam("scope", String.join(" ", clientRegistration.getScopes()))
//                .queryParam("state", state)
//                .build()
//                .toUriString();
//        log.info("Generated Authorization URL for provider {}: {}", providerName, authorizationUrl);
//        return authorizationUrl;
//    }

//    private ClientRegistration getClientRegistration(AuthProvider provider) {
//        String registrationId = provider.name().toLowerCase();
//
//        OAuth2ClientProperties.Registration registration =
//                clientProperties.getRegistration().get(registrationId);
//        OAuth2ClientProperties.Provider providerConfig =
//                clientProperties.getProvider().get(registrationId);
//
//        if (registration == null || providerConfig == null) {
//            throw new ApiException(ErrorCode.INVALID_PROVIDER_TYPE);
//        }
//
//        return ClientRegistration.withRegistrationId(registrationId)
//                .clientId(registration.getClientId())
//                .clientSecret(registration.getClientSecret())
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri(registration.getRedirectUri())
//                .scope(registration.getScope())
//                .authorizationUri(providerConfig.getAuthorizationUri())
//                .tokenUri(providerConfig.getTokenUri())
//                .userInfoUri(providerConfig.getUserInfoUri())
//                .userNameAttributeName(providerConfig.getUserNameAttribute())
//                .clientName(registrationId)
//                .build();
//    }


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
