package com.example.gamemate.domain.reply.repository;

import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByComment(Comment comment);

    List<Reply> findByParentReply(Reply reply);
}
