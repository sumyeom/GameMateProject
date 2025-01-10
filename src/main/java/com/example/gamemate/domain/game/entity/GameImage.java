package com.example.gamemate.domain.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "game_image")
public class GameImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    public GameImage(String fileName, String fileType, String filePath, Game game) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.game = game;
        if (game != null) {
            game.getImages().add(this);
        }
    }

}
