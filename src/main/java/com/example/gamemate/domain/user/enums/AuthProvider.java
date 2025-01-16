package com.example.gamemate.domain.user.enums;

import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.Getter;

@Getter
public enum AuthProvider {

    EMAIL("email"),
    GOOGLE("google"),
    KAKAO("kakao");

    private final String name;

    AuthProvider(String name) {
        this.name = name;
    }

    public static AuthProvider fromString(String provider) {
        for (AuthProvider authProvider : values()) {
            if (authProvider.getName().equalsIgnoreCase(provider)) {
                return authProvider;
            }
        }
        throw new ApiException(ErrorCode.INVALID_PROVIDER_TYPE);
    }

}
