package com.example.gamemate.domain.coupon.repository;

import com.example.gamemate.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    List<UserCoupon> findByUserId(Long userId);
//    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
}