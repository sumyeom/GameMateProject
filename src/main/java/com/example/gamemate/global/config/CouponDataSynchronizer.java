package com.example.gamemate.global.config;

import com.example.gamemate.domain.coupon.entity.Coupon;
import com.example.gamemate.domain.coupon.repository.CouponRepository;
import com.example.gamemate.domain.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponDataSynchronizer {

    private static final String COUPON_STOCK_KEY = "coupon:%d:stock";

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final StringRedisTemplate redisTemplate;

    public CouponDataSynchronizer(
            CouponRepository couponRepository,
            UserCouponRepository userCouponRepository,
            @Qualifier("couponRedisTemplate") StringRedisTemplate redisTemplate) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncCouponStock() {
        List<Coupon> coupons = couponRepository.findAll();
        for (Coupon coupon : coupons) {
            long issuedCount = userCouponRepository.countByCouponId(coupon.getId());
            long remainingStock = coupon.getTotalQuantity() - issuedCount;
            redisTemplate.opsForValue().set(getCouponStockKey(coupon.getId()),
                    String.valueOf(remainingStock));
        }
    }


    private String getCouponStockKey(Long couponId) {
        return String.format(COUPON_STOCK_KEY, couponId);
    }
}