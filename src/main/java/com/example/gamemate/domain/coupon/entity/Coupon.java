package com.example.gamemate.domain.coupon.entity;

import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupon")
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer issuedQuantity = 0;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @OneToMany(mappedBy = "coupon")
    private List<UserCoupon> userCoupons = new ArrayList<>();

    public Coupon(String code, String name, Integer discountAmount, Integer totalQuantity, LocalDateTime startAt, LocalDateTime expiredAt) {
        this.code = code;
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.startAt = startAt;
        this.expiredAt = expiredAt;
    }

    public boolean isIssuable() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startAt) && now.isBefore(expiredAt);
    }

    public boolean isExhausted() {
        return issuedQuantity >= totalQuantity;
    }

    public void incrementIssuedQuantity() {
        this.issuedQuantity++;
    }
}

