package com.example.gamemate.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식을 확인해주세요.")
    private final String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private final String name;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private final String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
//    @Size(min = 8, message = "비밀번호는 8글자 이상으로 입력해주세요.")
//    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 입력해주세요.")
    private final String password;

    @NotNull(message = "이메일 인증이 필요합니다.")
    private final Boolean isEmailVerified;

}
