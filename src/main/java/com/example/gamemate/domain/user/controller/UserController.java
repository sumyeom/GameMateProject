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

/**
 * 사용자와 관련된 요청을 처리하는 컨트롤러입니다.
 * 프로필 조회, 프로필 수정, 비밀번호 변경, 회원 탈퇴 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    /**
     * 사용자 프로필을 조회합니다.
     * @param id 조회할 사용자의 ID
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 조회된 사용자 프로필 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> findProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ProfileResponseDto responseDto = userService.findProfile(id, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 사용자의 프로필을 수정합니다.
     * @param id 수정할 사용자의 ID
     * @param requestDto 프로필 수정 요청 정보
     *                   (새 닉네임)
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 처리 결과 상태 코드
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.updateProfile(id, requestDto.getNewNickname(), customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     * @param id 변경할 사용자의 ID
     * @param requestDto 비밀번호 변경 요청 정보
     *                   (기존 비밀번호, 새 비밀번호)
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 처리 결과 상태 코드
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.updatePassword(id, requestDto.getOldPassword(), requestDto.getNewPassword(), customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 사용자의 탈퇴 요청을 처리합니다.
     * @param customUserDetails 현재 인증된 사용자 정보
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 처리 결과 상태 코드
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userService.withdrawUser(customUserDetails.getUser());
        authService.logout(customUserDetails.getUser(), request, response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
