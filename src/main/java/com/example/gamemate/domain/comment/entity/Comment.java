package com.example.gamemate.domain.comment.entity;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.reply.entity.Reply;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    public Comment(String content, Board board, User user) {
        this.content = content;
        this.board = board;
        this.user = user;
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
