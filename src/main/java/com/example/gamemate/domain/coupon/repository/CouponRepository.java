package com.example.gamemate.domain.coupon.repository;

import com.example.gamemate.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    boolean existsByCode(String code);
}
