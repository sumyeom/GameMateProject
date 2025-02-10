package com.example.gamemate.domain.coupon.dto;

import com.example.gamemate.domain.coupon.entity.Coupon;
import com.example.gamemate.domain.coupon.entity.UserCoupon;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponIssueResponseDto {

    private final Long id;
    private final String code;
    private final String name;
    private final Integer discountAmount;
    private final LocalDateTime issuedAt;
    private final LocalDateTime startAt;
    private final LocalDateTime expiredAt;
    private final Boolean isUsed;

    public CouponIssueResponseDto(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();
        this.id = userCoupon.getId();
        this.code = coupon.getCode();
        this.name = coupon.getName();
        this.discountAmount = coupon.getDiscountAmount();
        this.startAt = coupon.getStartAt();
        this.expiredAt = coupon.getExpiredAt();
        this.issuedAt = userCoupon.getIssuedAt();
        this.isUsed = userCoupon.getIsUsed();
    }
}

