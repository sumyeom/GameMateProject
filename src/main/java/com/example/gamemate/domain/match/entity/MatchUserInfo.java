package com.example.gamemate.domain.match.entity;

import com.example.gamemate.domain.match.enums.*;
import com.example.gamemate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
public class MatchUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ElementCollection
    @CollectionTable(name = "user_lanes")
    @Enumerated(EnumType.STRING)
    private Set<Lane> lanes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_purposes")
    @Enumerated(EnumType.STRING)
    private Set<Purpose> purposes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_play_times")
    @Enumerated(EnumType.STRING)
    private Set<PlayTimeRange> playTimeRanges = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private GameRank gameRank;

    @Column
    private Integer skillLevel;

    @Column
    private Boolean micUsage;

    @Column
    private String message;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient // DB에 저장하지 않고 런타임에만 사용
    private int matchScore;

    public MatchUserInfo() {
    }

    public MatchUserInfo(
            Gender gender,
            Set<Lane> lanes,
            Set<Purpose> purposes,
            Set<PlayTimeRange> playTimeRanges,
            GameRank gameRank,
            Integer skillLevel,
            Boolean micUsage,
            String message,
            User user
    ) {
        this.gender = gender;
        this.lanes = lanes;
        this.purposes = purposes;
        this.playTimeRanges = playTimeRanges;
        this.gameRank = gameRank;
        this.skillLevel = skillLevel;
        this.micUsage = micUsage;
        this.message = message;
        this.user = user;
    }

    public void updateMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
}
