package com.example.gamemate.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailVerificationCodeRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식을 확인해주세요.")
    private final String email;

}
