package com.example.gamemate.domain.coupon.dto;

import com.example.gamemate.domain.coupon.entity.Coupon;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponCreateResponseDto {
    private final Long id;
    private final String code;
    private final String name;
    private final Integer discountAmount;
    private final LocalDateTime startAt;
    private final LocalDateTime expiredAt;

    public CouponCreateResponseDto(Coupon coupon) {
        this.id = coupon.getId();
        this.code = coupon.getCode();
        this.name = coupon.getName();
        this.discountAmount = coupon.getDiscountAmount();
        this.startAt = coupon.getStartAt();
        this.expiredAt = coupon.getExpiredAt();
    }
}
