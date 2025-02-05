package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.*;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.AuthProvider;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        // 기존 사용자 중복 체크
        Optional<User> findUser = userRepository.findByEmail(requestDto.getEmail());

        if(findUser.isPresent()) {
            if(findUser.get().getUserStatus() == UserStatus.WITHDRAW) {
                throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
            }
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 이메일 인증 여부 확인
        if (!emailService.isEmailVerified(requestDto.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 비밀번호 암호화
        String rawPassword = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 새로운 사용자 생성 및 저장
        User newUser = new User(requestDto.getEmail(), requestDto.getName(), requestDto.getNickname(), encodedPassword);
        User savedUser = userRepository.save(newUser);

        return new SignupResponseDto(savedUser);
    }

    public LoginTokenResponseDto localLogin(LocalLoginRequestDto requestDto, HttpServletResponse response) {

        User findUser = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(findUser.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }

        if (findUser.getPassword().equals("OAUTH2_USER")) {
            throw new ApiException(ErrorCode.SOCIAL_PASSWORD_REQUIRED);
        }

        if(!passwordEncoder.matches(requestDto.getPassword(), findUser.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }
        return tokenService.generateLoginTokens(findUser, response);
    }

    public void setOAuth2Password(User user, String password) {
        if(user.getProvider() == AuthProvider.LOCAL) {
            throw new ApiException(ErrorCode.SOCIAL_PASSWORD_FORBIDDEN);
        }

        if(!"OAUTH2_USER".equals(user.getPassword())) {
            throw new ApiException(ErrorCode.SOCIAL_PASSWORD_ALREADY_SET);
        }

        String encodedPassword = passwordEncoder.encode(password);
        user.updatePassword(encodedPassword);
        userRepository.save(user);
    }

    public TokenRefreshResponseDto refreshAccessToken(String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String storedToken = refreshTokenService.getRefreshToken(email);

        if(!refreshToken.equals(storedToken)) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole());
        return new TokenRefreshResponseDto(newAccessToken);
    }

    public void logout(User user, HttpServletRequest request, HttpServletResponse response) {
        // 엑세스 토큰 블랙리스트 추가
        String accessToken = tokenService.extractToken(request);
        if(accessToken != null) {
            tokenService.blacklistToken(accessToken);
        }

        // 리프레시 토큰 처리
        String refreshToken = refreshTokenService.extractRefreshTokenFromCookie(request);
        if(refreshToken != null) {
            refreshTokenService.removeRefreshToken(user.getEmail(), response);
        }
    }
}
