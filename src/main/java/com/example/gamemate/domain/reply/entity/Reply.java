package com.example.gamemate.domain.reply.entity;

import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "reply")
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id")
    private Reply parentReply;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Reply(String content, Comment comment, User user) {
        this.content = content;
        this.comment = comment;
        this.user = user;
    }

    public Reply(String content, Comment comment, User user,  Reply parentReply) {
        this.content = content;
        this.comment = comment;
        this.user = user;
        this.parentReply = parentReply;
    }

    public void updateReply(String content){
        this.content = content;
    }
}
