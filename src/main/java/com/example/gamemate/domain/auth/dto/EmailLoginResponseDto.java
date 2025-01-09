package com.example.gamemate.domain.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailLoginResponseDto {

    private final String token;
    private final String email;
    private final String nickname;

}
