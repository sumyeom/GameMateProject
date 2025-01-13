package com.example.gamemate.domain.comment.service;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.comment.dto.CommentRequestDto;
import com.example.gamemate.domain.comment.dto.CommentResponseDto;
import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.comment.repository.CommentRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /**
     * 댓글 생성 메서드
     * @param boardId
     * @param requestDto
     * @return
     */
    @Transactional
    public CommentResponseDto createComment(Long boardId, CommentRequestDto requestDto) {
        // 게시글 조회
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        Comment comment = new Comment(requestDto.getContent(), findBoard);
        Comment createComment = commentRepository.save(comment);

        return new CommentResponseDto(
                createComment.getCommentId(),
                createComment.getContent(),
                createComment.getCreatedAt(),
                createComment.getModifiedAt()
        );
    }

    /**
     * 댓글 업데이트 메서드
     * @param id
     * @param requestDto
     */
    @Transactional
    public void updateComment(Long id, CommentRequestDto requestDto) {
        // 댓글 조회
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        findComment.updateComment(requestDto.getContent());
        commentRepository.save(findComment);
    }

    /**
     * 댓글 삭제 메서드
     * @param id
     */
    @Transactional
    public void deleteComment(Long id) {
        // 댓글 조회
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        commentRepository.delete(findComment);
    }
}
