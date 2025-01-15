package com.example.gamemate.domain.board.dto;

import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.comment.dto.CommentFindResponseDto;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardFindOneResponseDto {
    private final Long id;
    private final BoardCategory category;
    private final String title;
    private final String content;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final List<CommentFindResponseDto> comments;

    public BoardFindOneResponseDto(Long id, BoardCategory category, String title, String content, String nickname, LocalDateTime createdAt, LocalDateTime modifiedAt, List<CommentFindResponseDto> comments) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.comments = comments;
    }
}
