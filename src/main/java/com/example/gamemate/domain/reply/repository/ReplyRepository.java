package com.example.gamemate.domain.reply.repository;

import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByComment(Comment comment);

    List<Reply> findByParentReply(Reply reply);

    @Query("SELECT r FROM Reply r " +
            "LEFT JOIN FETCH r.parentReply pr " +
            "LEFT JOIN FETCH pr.user " +
            "WHERE r.comment.id IN :commentIds")
    List<Reply> findByCommentIdIn(@Param("commentIds") List<Long> commentIds);
}
