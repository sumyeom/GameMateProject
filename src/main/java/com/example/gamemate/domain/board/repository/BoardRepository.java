package com.example.gamemate.domain.board.repository;

import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardQuerydslRepository{
    Page<Board> findByCategory(String category, Pageable pageable);

    List<Board> findTop5ByOrderByViewsDesc();

    @Query("select b from Board b where b.id IN :boardIds order by b.views desc, b.createdAt desc")
    List<Board> findByIdInOrderByCreatedAtDesc(@Param("boardIds") List<Long> boardIds);

    List<Board> findTop5ByOrderByCreatedAtDesc();

    @Query("select b from Board b where b.id IN :boardIds order by b.views desc, b.createdAt desc")
    List<Board> findByIdInOrderByViewsDescCreatedAtDesc(@Param("boardIds") List<Long> boardIds);

    List<Board> findByIdIn(List<Long> boardIds);
}
