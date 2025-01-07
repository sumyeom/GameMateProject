package com.example.gamemate.domain.user.controller;

import com.example.gamemate.domain.user.dto.PasswordUpdateRequestDto;
import com.example.gamemate.domain.user.dto.ProfileResponseDto;
import com.example.gamemate.domain.user.dto.ProfileUpdateRequestDto;
import com.example.gamemate.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> findProfile(@PathVariable Long id) {
        ProfileResponseDto responseDto = userService.findProfile(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequestDto requestDto) {
        ProfileResponseDto responseDto = userService.updateProfile(id, requestDto.getNewNickname());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequestDto requestDto) {

        userService.updatePassword(id, requestDto.getNewPassword());
        return new ResponseEntity<>("비밀번호가 변경되었습니다.", HttpStatus.OK);
    }
}
