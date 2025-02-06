package com.example.gamemate.domain.auth.controller;

import com.example.gamemate.domain.auth.dto.*;
import com.example.gamemate.domain.auth.service.AuthService;
import com.example.gamemate.domain.auth.service.EmailService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody SignupRequestDto requestDto
    ) {
        SignupResponseDto responseDto = authService.signup(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/email/verification-request")
    public ResponseEntity<Void> sendVerificationEmail(
            @Valid @RequestBody EmailVerificationCodeRequestDto requestDto
    ) {
        emailService.sendVerificationEmail(requestDto.getEmail());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(
            @Valid @RequestBody EamilVerifyRequestDto requestDto
    ) {
        emailService.verifyEmail(requestDto.getEmail(), requestDto.getCode());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponseDto> localLogin(
            @Valid @RequestBody LocalLoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        LoginTokenResponseDto responseDto = authService.localLogin(requestDto, response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/oauth2/set-password")
    public ResponseEntity<Void> setPassword(
            @Valid @RequestBody OAuth2PasswordSetRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        authService.setOAuth2Password(customUserDetails.getUser(), requestDto.getPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(customUserDetails.getUser(), request, response);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @CookieValue(name = "refresh_token") String refreshToken
    ) {
        TokenRefreshResponseDto responseDto = authService.refreshAccessToken(refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}