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
@Table(name = "game_recommend_history")
public class GameRecommendHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String description;

    private Double matchingScore;

    @Column(length = 255)
    private String reasonForRecommendation;

    private Double star;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferences_id")
    private UserGamePreference userGamePreference;

    public GameRecommendHistory(User user, String title, String description, Double matchingScore, String reasonForRecommendation, Double star, UserGamePreference userGamePreference) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.matchingScore = matchingScore;
        this.reasonForRecommendation = reasonForRecommendation;
        this.star = star;
        this.userGamePreference = userGamePreference;

    }
}

