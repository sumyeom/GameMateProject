package com.example.gamemate.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuth2PasswordSetRequestDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
//    @Size(min = 8, message = "비밀번호는 8글자 이상으로 입력해주세요.")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[!?@#$%^&*+=-])(?=.*\\d).{8,15}$", message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 입력해주세요.")
    private final String password;

}
