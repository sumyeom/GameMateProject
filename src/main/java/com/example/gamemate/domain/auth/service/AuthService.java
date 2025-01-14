package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.*;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.AuthProvider;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        Optional<User> findUser = userRepository.findByEmail(requestDto.getEmail());
        if(findUser.isPresent()) {
            if(findUser.get().getUserStatus() == UserStatus.WITHDRAW) {
                throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
            }
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }

        String rawPassword = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(requestDto.getEmail(), requestDto.getName(), requestDto.getNickname(), encodedPassword);
        User savedUser = userRepository.save(user);

        return new SignupResponseDto(savedUser);
    }

    public EmailLoginResponseDto emailLogin(EmailLoginRequestDto requestDto, HttpServletResponse response) {

        User findUser = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(findUser.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }

        if(!passwordEncoder.matches(requestDto.getPassword(), findUser.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(findUser.getEmail(), findUser.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(findUser.getEmail());

        findUser.updateRefreshToken(refreshToken);
        userRepository.save(findUser);

        addRefreshTokenToCookie(response, refreshToken);

        return new EmailLoginResponseDto(accessToken);
    }

    public TokenRefreshResponseDto refreshAccessToken(String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(!refreshToken.equals(user.getRefreshToken())) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole());
        return new TokenRefreshResponseDto(newAccessToken);
    }

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
                .orElseGet(()-> {
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

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if(refreshToken != null) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            userRepository.findByEmail(email).ifPresent(user -> {
                user.removeRefreshToken();
                userRepository.save(user);
            });

            Cookie cookie = new Cookie("refresh_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(3 * 24 * 60 * 60); // 3일
        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
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
