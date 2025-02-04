package com.example.gamemate.domain.reply.service;

import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.comment.repository.CommentRepository;
import com.example.gamemate.domain.reply.dto.ReplyRequestDto;
import com.example.gamemate.domain.reply.dto.ReplyResponseDto;
import com.example.gamemate.domain.reply.entity.Reply;
import com.example.gamemate.domain.reply.repository.ReplyRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.eventListener.event.MatchCreatedEvent;
import com.example.gamemate.global.eventListener.event.ReplyCreatedEvent;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher publisher;

    /**
     * 대댓글 생성 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param commentId 댓글 식별자
     * @param requestDto 댓글 생성 Dto
     * @return 대댓글 생성 정보 Dto
     */
    @Transactional
    public ReplyResponseDto createReply(User loginUser, Long commentId, ReplyRequestDto requestDto) {
        //댓글 조회
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        Reply newReply;
        // 부모 대댓글 null 일 경우
        if(requestDto.getParentReplyId()==null){
            newReply = new Reply(requestDto.getContent(), findComment, loginUser);
            Reply createReply = replyRepository.save(newReply);
            publisher.publishEvent(new ReplyCreatedEvent(this, createReply));

            return new ReplyResponseDto(
                    createReply.getId(),
                    createReply.getComment().getId(),
                    createReply.getContent(),
                    createReply.getCreatedAt(),
                    createReply.getModifiedAt()
            );
        }else{
            //대댓글 조회
            Reply findParentReply = replyRepository.findById(requestDto.getParentReplyId())
                    .orElseThrow(()-> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
            newReply = new Reply(requestDto.getContent(), findComment, loginUser, findParentReply);
            Reply createReply = replyRepository.save(newReply);
            publisher.publishEvent(new ReplyCreatedEvent(this, createReply));

            return new ReplyResponseDto(
                    createReply.getId(),
                    createReply.getComment().getId(),
                    createReply.getParentReply().getId(),
                    createReply.getContent(),
                    createReply.getCreatedAt(),
                    createReply.getModifiedAt()
            );
        }
    }

    /**
     * 대댓글 업데이트 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param id 대댓글 식별자
     * @param requestDto 대댓글 업데이트 Dto
     */
    @Transactional
    public void updateReply(User loginUser, Long id, ReplyRequestDto requestDto) {
        // 대댓글 조회
        Reply findReply = replyRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 대댓글 작성자와 로그인 유저 확인
        if(!findReply.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        findReply.updateReply(requestDto.getContent());
        replyRepository.save(findReply);
    }

    /**
     * 대댓글 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param id 대댓글 식별자
     */
    @Transactional
    public void deleteReply(User loginUser, Long id) {
        // 대댓글 조회
        Reply findReply = replyRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 대댓글 작성자와 로그인 유저 확인
        if(!findReply.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        replyRepository.delete(findReply);
    }
}
