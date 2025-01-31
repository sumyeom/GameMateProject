package com.example.gamemate.domain.reply.controller;

import com.example.gamemate.domain.reply.dto.ReplyRequestDto;
import com.example.gamemate.domain.reply.dto.ReplyResponseDto;
import com.example.gamemate.domain.reply.service.ReplyService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards/{boardId}/comments/{commentId}/replies")
public class ReplyController {

    private final ReplyService replyService;

    /**
     * 대댓글 생성 API 입니다.
     *
     * @param commentId 댓글 식별자
     * @param requestDto 대댓글 생성 Dto
     * @param customUserDetails 인증된 사용자
     * @return 대댓글 생성 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<ReplyResponseDto> createReply(
            @PathVariable Long commentId,
            @Valid  @RequestBody ReplyRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        ReplyResponseDto dto = replyService.createReply(customUserDetails.getUser(), commentId, requestDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * 대댓글 수정 API 입니다.
     *
     * @param id 대댓글 식별자
     * @param requestDto 업데이트할 대댓글 Dto
     * @param customUserDetails 인증된 사용자
     * @return Void
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateReply(
        @PathVariable Long id,
        @Valid @RequestBody ReplyRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        replyService.updateReply(customUserDetails.getUser(), id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 대댓글 삭제 API 입니다.
     *
     * @param id 대댓글 식별자
     * @param customUserDetails 인증된 사용자
     * @return Void
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        replyService.deleteReply(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

