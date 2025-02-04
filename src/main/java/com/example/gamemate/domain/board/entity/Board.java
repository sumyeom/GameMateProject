package com.example.gamemate.domain.board.entity;

import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.boardImage.entity.BoardImage;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "board")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoardCategory category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private int views = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board")
    private List<BoardImage> boardImages;

    public Board(BoardCategory category, String title, String content, User user) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void updateBoard(BoardCategory category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void updateViewCount(int views){
        this.views=views;
    }
}
