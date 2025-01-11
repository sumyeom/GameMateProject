package com.example.gamemate.domain.comment.repository;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByBoard(Board findBoard, Pageable pageable);
}
