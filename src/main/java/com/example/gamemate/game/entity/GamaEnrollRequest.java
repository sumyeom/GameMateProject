package com.example.gamemate.game.entity;

import com.example.gamemate.base.BaseEntity;
import com.example.gamemate.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "game_enroll_request")
@AllArgsConstructor
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

    @Column(name = "platform", length = 255)
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
