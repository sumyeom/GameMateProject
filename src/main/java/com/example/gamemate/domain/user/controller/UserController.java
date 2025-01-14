package com.example.gamemate.domain.user.controller;

import com.example.gamemate.domain.auth.service.AuthService;
import com.example.gamemate.domain.user.dto.PasswordUpdateRequestDto;
import com.example.gamemate.domain.user.dto.ProfileResponseDto;
import com.example.gamemate.domain.user.dto.ProfileUpdateRequestDto;
import com.example.gamemate.domain.user.service.UserService;
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
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> findProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ProfileResponseDto responseDto = userService.findProfile(id, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.updateProfile(id, requestDto.getNewNickname(), customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.updatePassword(id, requestDto.getOldPassword(), requestDto.getNewPassword(), customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userService.withdrawUser(customUserDetails.getUser());
        authService.logout(request, response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
