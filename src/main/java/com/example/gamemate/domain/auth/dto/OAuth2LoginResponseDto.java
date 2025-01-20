package com.example.gamemate.domain.auth.dto;

import com.example.gamemate.domain.user.enums.AuthProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuth2LoginResponseDto {

    private final String providerId;
    private final String email;
    private final String name;
    private final AuthProvider provider;

}
