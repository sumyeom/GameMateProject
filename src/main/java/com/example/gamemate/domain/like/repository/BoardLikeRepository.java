package com.example.gamemate.domain.like.repository;

import com.example.gamemate.domain.like.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    @Query("SELECT bl FROM BoardLike bl WHERE bl.board.boardId = :boardId AND bl.user.id = :userId")
    Optional<BoardLike> findByBoardIdAndUserId(@Param("boardId") Long boardId, @Param("userId") Long userId);

    Long countByBoardBoardIdAndStatus(Long boardId, Integer status);


}
