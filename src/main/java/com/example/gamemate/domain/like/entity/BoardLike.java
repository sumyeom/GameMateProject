package com.example.gamemate.domain.like.entity;

import com.example.gamemate.domain.board.entity.Board;
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

    @Column(nullable = false)
    private Integer status; // 1: 좋아요, -1: 싫어요, 0: 무반응

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardLike(Integer status, User user, Board board) {
        this.status = status;
        this.user = user;
        this.board = board;
    }

    // 좋아요 상태 변경을 위한 메서드
    public void changeStatus(Integer status) {
        this.status = status;
    }
}
