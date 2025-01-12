package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.*;
import com.example.gamemate.domain.user.entity.User;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
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
}
