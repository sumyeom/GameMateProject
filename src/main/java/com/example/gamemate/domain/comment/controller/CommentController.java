package com.example.gamemate.domain.comment.controller;

import com.example.gamemate.domain.comment.dto.CommentFindResponseDto;
import com.example.gamemate.domain.comment.dto.CommentRequestDto;
import com.example.gamemate.domain.comment.dto.CommentResponseDto;
import com.example.gamemate.domain.comment.service.CommentService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 API 입니다.
     *
     * @param boardId 게시글 식별자
     * @param requestDto 댓글 요청 Dto
     * @return 생성된 댓글 정보를 포함한 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        CommentResponseDto dto = commentService.createComment(customUserDetails.getUser(),boardId, requestDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * 댓글/대댓글 조회 입니다.
     *
     * @param boardId 댓글 식별자
     * @param page 페이지 번호(기본값 : 0)
     * @return 댓글 리스트 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<CommentFindResponseDto>> getComments(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page
    ){
        List<CommentFindResponseDto> dtos = commentService.getComments(boardId, page);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * 댓글 수정 API 입니다.
     *
     * @param id 댓글 식별자
     * @param requestDto 업데이트할 댓글 Dto
     * @return Void
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        commentService.updateComment(customUserDetails.getUser(), id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 댓글 삭제 API 입니다.
     *
     * @param id 댓글 식별자
     * @param customUserDetails 인증된 사용자 정보
     * @return Void
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        commentService.deleteComment(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
