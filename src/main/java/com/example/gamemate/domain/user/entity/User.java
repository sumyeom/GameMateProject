package com.example.gamemate.domain.user.entity;

import com.example.gamemate.global.common.BaseEntity;
import com.example.gamemate.domain.user.enums.Role;
import com.example.gamemate.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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

    public User(String email, String name, String nickname, String password) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
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

    public void deleteSoftly() {
        markDeletedAt();
    }

}
