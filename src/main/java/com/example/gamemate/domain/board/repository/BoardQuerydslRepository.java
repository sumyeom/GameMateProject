package com.example.gamemate.domain.board.repository;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardQuerydslRepository {
    Page<Board> searchBoardQuerydsl(BoardCategory category, String title, String content, Pageable pageable);
}
