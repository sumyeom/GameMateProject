package com.example.gamemate.domain.auth.controller;

import com.example.gamemate.domain.auth.dto.*;
import com.example.gamemate.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody SignupRequestDto requestDto
    ) {
        SignupResponseDto responseDto = authService.signup(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<EmailLoginResponseDto> emailLogin(
            @Valid @RequestBody EmailLoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        EmailLoginResponseDto responseDto = authService.emailLogin(requestDto, response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(request, response);
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
