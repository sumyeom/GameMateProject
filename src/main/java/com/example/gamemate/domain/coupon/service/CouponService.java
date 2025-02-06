package com.example.gamemate.domain.coupon.service;

import com.example.gamemate.domain.coupon.dto.CouponCreateRequestDto;
import com.example.gamemate.domain.coupon.dto.CouponCreateResponseDto;
import com.example.gamemate.domain.coupon.dto.CouponIssueResponseDto;
import com.example.gamemate.domain.coupon.entity.Coupon;
import com.example.gamemate.domain.coupon.entity.UserCoupon;
import com.example.gamemate.domain.coupon.repository.CouponRepository;
import com.example.gamemate.domain.coupon.repository.UserCouponRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.Role;
import com.example.gamemate.global.common.aop.DistributedLock;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponCreateResponseDto createCoupon(CouponCreateRequestDto requestDto, User loginUser) {
        // 관리자 권한 체크
        if (!loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // 쿠폰 코드 중복 확인
        if (couponRepository.existsByCode(requestDto.getCode())) {
            throw new ApiException(ErrorCode.COUPON_CODE_DUPLICATED);
        }

        // 쿠폰 사용 기간 유효성 검증
        validateCouponDates(requestDto.getStartAt(), requestDto.getExpiredAt());

        // 쿠폰 생성
        Coupon coupon = new Coupon(requestDto.getCode(), requestDto.getName(), requestDto.getDiscountAmount(), requestDto.getQuantity(), requestDto.getStartAt(), requestDto.getExpiredAt());
        Coupon savedCoupon = couponRepository.save(coupon);

        return new CouponCreateResponseDto(savedCoupon);
    }

    @DistributedLock(key = "'LOCK:coupon:' + #couponId")
    public CouponIssueResponseDto issueCoupon(Long couponId, User loginUser) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApiException(ErrorCode.COUPON_NOT_FOUND));

        // 발급 가능 체크
        if (!coupon.isIssuable()) {
            throw new ApiException(ErrorCode.COUPON_NOT_ISSUABLE);
        }

        // 중복 발급 체크
        if (userCouponRepository.existsByUserIdAndCouponId(loginUser.getId(), couponId)) {
            throw new ApiException(ErrorCode.COUPON_ALREADY_ISSUED);
        }

        // 수량 체크
        if (coupon.isExhausted()) {
            throw new ApiException(ErrorCode.COUPON_EXHAUSTED);
        }

        // 쿠폰 발급
        coupon.incrementIssuedQuantity();
        UserCoupon userCoupon = new UserCoupon(loginUser, coupon);
        UserCoupon savedUserCoupon = userCouponRepository.save(userCoupon);

        return new CouponIssueResponseDto(savedUserCoupon);
    }

    @Transactional(readOnly = true)
    public List<CouponIssueResponseDto> findMyCoupons(User loginUser) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(loginUser.getId());
        return userCoupons.stream()
                .map(CouponIssueResponseDto::new)
                .collect(Collectors.toList());
    }

    public void useCoupon(Long userCouponId, User loginUser) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(()-> new ApiException(ErrorCode.COUPON_NOT_FOUND));

        // 본인 쿠폰인지 확인
        if (!userCoupon.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // 이미 사용된 쿠폰인지 확인
        if (userCoupon.getIsUsed()) {
            throw new ApiException(ErrorCode.COUPON_ALREADY_USED);
        }

        // 쿠폰 유효기간 확인
        if (LocalDateTime.now().isAfter(userCoupon.getCoupon().getExpiredAt())) {
            throw new ApiException(ErrorCode.COUPON_EXPIRED);
        }

        // 쿠폰 사용 처리
        userCoupon.updateIsUsed(true);
        userCoupon.updateUsedAt();
    }

    private void validateCouponDates(LocalDateTime startAt, LocalDateTime expiredAt) {
        if (startAt.isAfter(expiredAt)) {
            throw new ApiException(ErrorCode.INVALID_COUPON_DATE);
        }
    }
}
