package com.example.gamemate.domain.game.entity;

import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "game_enroll_request")
@NoArgsConstructor
public class GamaEnrollRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255, unique = false)
    private String title;

    @Column(name = "genre", length = 10)
    private String genre;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "platform", length = 20)
    private String platform;

//    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
//    private List<GameImage> gameImages = new ArrayList<>();

    @Column(name = "is_accepted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAccepted = false;

    public GamaEnrollRequest(String title, String genre, String platform, String description ) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;

    }

    public void updateGameEnroll(String title, String genre, String platform, String description,Boolean isAccepted) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;
        this.isAccepted = isAccepted;
    }


}
