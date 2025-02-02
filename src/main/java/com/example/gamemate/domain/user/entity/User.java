package com.example.gamemate.domain.user.entity;

import com.example.gamemate.domain.coupon.entity.UserCoupon;
import com.example.gamemate.domain.follow.entity.Follow;
import com.example.gamemate.domain.user.enums.AuthProvider;
import com.example.gamemate.global.common.BaseEntity;
import com.example.gamemate.domain.user.enums.Role;
import com.example.gamemate.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`user`")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean isPremium;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followingList;

    @OneToMany(mappedBy = "followee")
    private List<Follow> followerList;

    @OneToMany(mappedBy = "user")
    private List<UserCoupon> userCoupons = new ArrayList<>();

    // 이메일 로그인용 생성자
    public User(String email, String name, String nickname, String password) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.provider = AuthProvider.LOCAL;
        this.providerId = null;
        this.role = Role.USER;
        this.isPremium = false;
        this.userStatus = UserStatus.ACTIVE;
        this.refreshToken = null;
    }

    // OAuth용 생성자
    public User(String email, String name, String nickname, AuthProvider provider, String providerId) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = "OAUTH2_USER";
        this.provider = provider;
        this.providerId = providerId;
        this.role = Role.USER;
        this.isPremium = false;
        this.userStatus = UserStatus.ACTIVE;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateProfile(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateUserStatus(UserStatus status) {
        this.userStatus = status;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void removeRefreshToken() {
        this.refreshToken = null;
    }

    public void integrateOAuthProvider(AuthProvider provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }

}
