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
import java.util.Map;
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
     * 댓글 생성 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param boardId 게시글 식별자
     * @param requestDto 댓글 생성할 requestDto
     * @return CommentResponseDto
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
                createComment.getId(),
                createComment.getContent(),
                createComment.getUser().getNickname(),
                createComment.getCreatedAt(),
                createComment.getModifiedAt()
        );
    }

    /**
     * 댓글 업데이트 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param id 댓글 식별자
     * @param requestDto 업데이트할 댓글 dto
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
     * 댓글 삭제 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param id 댓글 식별자
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
     * 댓글 조회 메서드입니다.
     *
     * @param boardId 게시글 식별자
     * @param page 페이지 번호(기본값 : 0)
     * @return Comment 조회 Do
     */
    public List<CommentFindResponseDto> getComments(Long boardId, int page) {
        // page는 댓글 페이지네이션을 위해 필요
        Pageable pageable = PageRequest.of(page, ListSize.COMMENT_LIST_SIZE.getSize(), Sort.by(Sort.Order.asc("createdAt")));

        //게시글 조회
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_NOT_FOUND));

        // 댓글 조회

        List<Comment> comments = commentRepository.findByBoard(findBoard, pageable).getContent();
        List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());

        List<Reply> replies = replyRepository.findByCommentIdIn(commentIds);
        //Page<Comment> comments = commentRepository.findByBoard(findBoard,pageable);

        //return comments.stream()
        //        .map(this::convertCommentDto)
        //        .collect(Collectors.toList());
        Map<Long, List<Reply>> repliesByCommentId = replies.stream()
                .collect(Collectors.groupingBy(reply -> reply.getComment().getId()));

        return comments.stream()
                .map(comment -> convertCommentDto(comment, repliesByCommentId.getOrDefault(comment.getId(), List.of())))
                .collect(Collectors.toList());
    }

    /**
     * 댓글 Dto 변환입니다.
     *
     * @param comment comment
     * @return 댓글 조회 Dto
     */
    private CommentFindResponseDto convertCommentDto(Comment comment, List<Reply> replies) {
//        List<ReplyFindResponseDto> replyDtos = Optional.ofNullable(replyRepository.findByComment(comment))
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(this::convertReplyDto)
//                .collect(Collectors.toList());
        List<ReplyFindResponseDto> replyDtos = replies.stream()
                .map(this::convertReplyDto)
                .collect(Collectors.toList());
        return new CommentFindResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedAt(),
                comment.getModifiedAt(),
                replyDtos
        );
    }

    /**
     * 대댓글 Dto 변환입니다.
     *
     * @param reply 대댓글
     * @return 대댓글 조회 Dto
     */
    private ReplyFindResponseDto convertReplyDto(Reply reply) {
        String findUserName = reply.getParentReply() == null ? null : reply.getParentReply().getUser().getNickname();
        return new ReplyFindResponseDto(
                reply.getId(),
                findUserName,
                reply.getContent(),
                reply.getCreatedAt(),
                reply.getModifiedAt()
        );
    }
}
