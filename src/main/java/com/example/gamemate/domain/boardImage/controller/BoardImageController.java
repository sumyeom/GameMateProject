package com.example.gamemate.domain.boardImage.controller;

import com.example.gamemate.domain.boardImage.service.BoardImageService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards/{boardId}/files")
public class BoardImageController {

    private final BoardImageService boardImageService;

    /**
     * 게시글 첨부파일 추가 API
     * @param boardId 보드 식별자
     * @param image 게시글에 첨부할 이미지
     * @param customUserDetails 인증된 사용자 정보
     * @return String ResponseEntity
     * @throws IOException 오류 발생
     */
    @PostMapping
    public ResponseEntity<String> createBoardImage(
        @PathVariable Long boardId,
        @RequestParam("image") MultipartFile image,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws IOException {
        boardImageService.createBoardImage(customUserDetails.getUser(), boardId, image);
        return new ResponseEntity<>("업로드", HttpStatus.CREATED);
    }

    /**
     * 게시글 첨부파일 수정 API
     *
     * @param id 이미지 식별자
     * @param image 수정할 게시글 이미지
     * @param customUserDetails 인증된 사용자 정보
     * @return Void ResponseEntity
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBoardImage(
        @PathVariable Long id,
        @RequestParam("image") MultipartFile image,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws IOException {
        boardImageService.updateBoardImage(customUserDetails.getUser(), id, image);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
     * 이미지 삭제 API
     *
     * @param id 이미지 식별자
     * @param customUserDetails 인증된 사용자 정보
     * @return Void ResponseEntity
     * @throws IOException 오류 발생
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardImage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws IOException {
        boardImageService.deleteImage(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
