package com.example.gamemate.domain.boardImage.repository;

import com.example.gamemate.domain.boardImage.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
}
