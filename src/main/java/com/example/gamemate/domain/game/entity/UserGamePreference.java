package com.example.gamemate.domain.game.entity;

import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_game_preference")
public class UserGamePreference extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 10)
    private String preferredGenres;

    @Column(length = 255)
    private String playStyle;

    @Column(length = 10)
    private String playTime;

    @Column(length = 10)
    private String difficulty;

    @Column(length = 20)
    private String platform;

    @Column(length = 255)
    private String extraRequest;

    public UserGamePreference(User user, String preferredGenres, String playStyle, String playTime, String difficulty, String platform, String extraRequest){
        this.user =user;
        this.preferredGenres = preferredGenres;
        this.playStyle = playStyle;
        this.playTime = playTime;
        this.difficulty = difficulty;
        this.platform = platform;
        this.extraRequest =extraRequest;
    }
}

