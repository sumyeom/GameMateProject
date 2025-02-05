package com.example.gamemate.domain.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginTokenResponseDto {

    private final String accessToken;

}
