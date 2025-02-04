package com.example.gamemate.domain.coupon.controller;

import com.example.gamemate.domain.coupon.dto.CouponCreateRequestDto;
import com.example.gamemate.domain.coupon.dto.CouponCreateResponseDto;
import com.example.gamemate.domain.coupon.dto.CouponIssueResponseDto;
import com.example.gamemate.domain.coupon.service.CouponService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/create")
    public ResponseEntity<CouponCreateResponseDto> createCoupon(
            @Valid @RequestBody CouponCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CouponCreateResponseDto responseDto = couponService.createCoupon(requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/{couponId}/issue")
    public ResponseEntity<CouponIssueResponseDto> issueCoupon(
            @PathVariable Long couponId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CouponIssueResponseDto responseDto = couponService.issueCoupon(couponId, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<CouponIssueResponseDto>> findMyCoupons(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<CouponIssueResponseDto> responseDtos = couponService.findMyCoupons(customUserDetails.getUser());
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @PostMapping("/user-coupons/{userCouponId}/use")
    public ResponseEntity<Void> useCoupon(
            @PathVariable Long userCouponId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        couponService.useCoupon(userCouponId, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

