package com.example.gamemate.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileUpdateRequestDto {

    @NotBlank(message = "새로운 닉네임을 입력해주세요.")
    private final String newNickname;

}
