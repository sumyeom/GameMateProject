package com.example.gamemate.domain.match.entity;

import com.example.gamemate.domain.match.enums.*;
import com.example.gamemate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
public class MatchDesiredInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ElementCollection
    @CollectionTable(name = "desired_lanes")
    @Enumerated(EnumType.STRING)
    private Set<Lane> lanes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "desired_purposes")
    @Enumerated(EnumType.STRING)
    private Set<Purpose> purposes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "desired_play_times")
    @Enumerated(EnumType.STRING)
    private Set<PlayTimeRange> playTimeRanges = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private GameRank gameRank;

    @Column
    private Integer skillLevel;

    @Column
    private Boolean micUsage;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public MatchDesiredInfo() {
    }

    public MatchDesiredInfo(
            Gender gender,
            Set<Lane> lanes,
            Set<Purpose> purposes,
            Set<PlayTimeRange> playTimeRanges,
            GameRank gameRank,
            Integer skillLevel,
            Boolean micUsage,
            User user
    ) {
        this.gender = gender;
        this.lanes = lanes;
        this.purposes = purposes;
        this.playTimeRanges = playTimeRanges;
        this.gameRank = gameRank;
        this.skillLevel = skillLevel;
        this.micUsage = micUsage;
        this.user = user;
    }
}
