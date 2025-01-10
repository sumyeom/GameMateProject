package com.example.gamemate.domain.comment.repository;

import com.example.gamemate.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
