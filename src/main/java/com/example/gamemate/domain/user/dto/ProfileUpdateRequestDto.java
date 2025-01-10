package com.example.gamemate.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProfileUpdateRequestDto {

    @NotBlank
    private String newNickname;

}
