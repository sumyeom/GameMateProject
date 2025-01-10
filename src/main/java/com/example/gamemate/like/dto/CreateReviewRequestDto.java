package com.example.gamemate.like.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReviewRequestDto {
    private String status;

    public CreateReviewRequestDto(String status){
        this.status =status;
    }
}
