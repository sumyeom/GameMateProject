package com.example.gamemate.domain.comment.service;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.ListSize;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.comment.dto.CommentFindResponseDto;
import com.example.gamemate.domain.comment.dto.CommentRequestDto;
import com.example.gamemate.domain.comment.dto.CommentResponseDto;
import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.comment.repository.CommentRepository;
import com.example.gamemate.domain.reply.dto.ReplyFindResponseDto;
import com.example.gamemate.domain.reply.entity.Reply;
import com.example.gamemate.domain.reply.repository.ReplyRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.eventListener.event.CommentCreatedEvent;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final ApplicationEventPublisher publisher;

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

        publisher.publishEvent(new CommentCreatedEvent(this, createComment));

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

    /**
     * 댓글 조회 메서드
     * @param boardId
     * @param page
     * @return
     */
    public List<CommentFindResponseDto> getComments(Long boardId, int page) {
        // page는 댓글 페이지네이션을 위해 필요
        Pageable pageable = PageRequest.of(page, ListSize.COMMENT_LIST_SIZE.getSize(), Sort.by(Sort.Order.asc("createdAt")));

        //게시글 조회
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_NOT_FOUND));

        // 댓글 조회
        Page<Comment> comments = commentRepository.findByBoard(findBoard,pageable);

        return comments.stream()
                .map(this::convertCommentDto)
                .collect(Collectors.toList());
    }

    private CommentFindResponseDto convertCommentDto(Comment comment) {
        List<ReplyFindResponseDto> replyDtos = Optional.ofNullable(replyRepository.findByComment(comment))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertReplyDto)
                .collect(Collectors.toList());
        return new CommentFindResponseDto(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedAt(),
                comment.getModifiedAt(),
                replyDtos
        );
    }

    private ReplyFindResponseDto convertReplyDto(Reply reply) {
        String findUserName = reply.getParentReply() == null ? null : reply.getParentReply().getUser().getNickname();
        return new ReplyFindResponseDto(
                reply.getReplyId(),
                findUserName,
                reply.getContent(),
                reply.getCreatedAt(),
                reply.getModifiedAt()
        );
    }
}
