package com.example.gamemate.domain.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CouponCreateRequestDto {

    @NotBlank(message = "쿠폰 코드를 입력해주세요.")
    private final String code;

    @NotBlank(message = "쿠폰 이름을 입력해주세요.")
    private final String name;

    @Positive(message = "할인 금액을 양수로 입력해주세요.")
    @NotNull(message = "할인 금액을 입력해주세요.")
    private final Integer discountAmount;

    @NotNull(message = "시작 시간을 입력해주세요.")
    private final LocalDateTime startAt;

    @NotNull(message = "종료 시간을 입력해주세요.")
    private final LocalDateTime expiredAt;

    @NotNull(message = "쿠폰 수량을 입력해주세요.")
    private final Integer quantity;

}

