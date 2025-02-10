package com.example.gamemate.domain.like.entity;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.like.enums.LikeStatus;
import com.example.gamemate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardLike(LikeStatus status, User user, Board board) {
        this.status = status;
        this.user = user;
        this.board = board;
    }

    // 좋아요 상태 변경을 위한 메서드
    public void changeStatus(LikeStatus status) {
        this.status = status;
    }
}
