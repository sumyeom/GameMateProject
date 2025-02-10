package com.example.gamemate.domain.board.dto;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.boardImage.entity.BoardImage;
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
    private final List<String> fileName;
    private final List<String> imageUrl;

    public BoardFindOneResponseDto(Long id, BoardCategory category, String title, String content, String nickname, LocalDateTime createdAt, LocalDateTime modifiedAt, List<String> fileName, List<String> imageUrl) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.fileName = fileName;
        this.imageUrl = imageUrl;
    }

    public BoardFindOneResponseDto(Board board) {
        this.id = board.getId();
        this.category = board.getCategory();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.nickname = board.getUser().getNickname();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.fileName = board.getBoardImages().isEmpty() ? null :
                board.getBoardImages().stream()
                        .map(BoardImage::getFileName)
                        .toList();
        this.imageUrl = board.getBoardImages().isEmpty() ? null :
                board.getBoardImages().stream()
                        .map(BoardImage::getFilePath)
                        .toList();
    }
}
