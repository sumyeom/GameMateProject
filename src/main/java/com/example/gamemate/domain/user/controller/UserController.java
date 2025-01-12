package com.example.gamemate.domain.user.controller;

import com.example.gamemate.domain.auth.service.AuthService;
import com.example.gamemate.domain.user.dto.PasswordUpdateRequestDto;
import com.example.gamemate.domain.user.dto.ProfileResponseDto;
import com.example.gamemate.domain.user.dto.ProfileUpdateRequestDto;
import com.example.gamemate.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final AuthService authService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> findProfile(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        String jwtToken = token.substring(7);
        ProfileResponseDto responseDto = userService.findProfile(id, jwtToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        ProfileResponseDto responseDto = userService.updateProfile(id, requestDto.getNewNickname(), jwtToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequestDto requestDto,
            @RequestHeader("Authorization") String token) {

        String jwtToken = token.substring(7);
        userService.updatePassword(id, requestDto.getOldPassword(), requestDto.getNewPassword(), jwtToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String jwtToken = token.substring(7);
        userService.withdrawUser(jwtToken);

        authService.logout(request, response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
