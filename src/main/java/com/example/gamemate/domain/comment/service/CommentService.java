package com.example.gamemate.domain.comment.service;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.comment.dto.CommentRequestDto;
import com.example.gamemate.domain.comment.dto.CommentResponseDto;
import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.comment.repository.CommentRepository;
import com.example.gamemate.domain.user.entity.User;
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
    public CommentResponseDto createComment(User loginUser, Long boardId, CommentRequestDto requestDto) {
        // 게시글 조회
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        Comment comment = new Comment(requestDto.getContent(), findBoard, loginUser);
        Comment createComment = commentRepository.save(comment);

        return new CommentResponseDto(
                createComment.getCommentId(),
                createComment.getContent(),
                createComment.getUser().getNickname(),
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
    public void updateComment(User loginUser, Long id, CommentRequestDto requestDto) {
        // 댓글 조회
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자와 로그인한 유저 확인
        if(!findComment.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        findComment.updateComment(requestDto.getContent());
        commentRepository.save(findComment);
    }

    /**
     * 댓글 삭제 메서드
     * @param id
     */
    @Transactional
    public void deleteComment(User loginUser, Long id) {
        // 댓글 조회
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자와 로그인한 유저 확인
        if(!findComment.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        commentRepository.delete(findComment);
    }
}
