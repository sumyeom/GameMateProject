package com.example.gamemate.domain.boardImage.entity;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "board_image")
public class BoardImage extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    public BoardImage(String fileName, String fileType, String filePath, Long fileSize, Board board) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.board = board;
    }

    public void updateBoardImage(String fileName, String fileType, String filePath, Long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
}
