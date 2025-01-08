package com.example.gamemate.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "game")
@AllArgsConstructor
public class Game   {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255 ,unique = true)
    private String title;

    @Column(name = "genre", nullable = false, length = 10)
    private String genre;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "platform", nullable = false, length = 255)
    private String platform;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameImage> gameImages = new ArrayList<>();

    public Game(String title, String genre, String description, String platform) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.platform = platform;
    }

}
