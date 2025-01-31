package com.example.gamemate.domain.board.controller;

import com.example.gamemate.domain.board.dto.BoardRequestDto;
import com.example.gamemate.domain.board.dto.BoardResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindOneResponseDto;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.service.BoardService;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 관련 API를 처리하는 컨트롤러 클래스입니다.
 * 게시글의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 생성 API 입니다.
     *
     * @param dto 게시글 생성 dto
     * @param customUserDetails 인증 정보
     * @return 생성된 게시글 정보를 포함한 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @Valid @RequestBody BoardRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){

        BoardResponseDto responseDto = boardService.createBoard(customUserDetails.getUser(), dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 게시글 조회하고 검색하는 API 입니다.
     *
     * @param page 페이지 번호(기본값 : 0)
     * @param category 카테고리 종류
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @return 게시글 목록을 포함한 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<BoardFindAllResponseDto>> findAllBoards(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String content
    ) {

        BoardCategory boardCategory = null;
        if (category != null) {
            boardCategory = BoardCategory.fromName(category);
        }

        List<BoardFindAllResponseDto> dtos = boardService.findAllBoards(page,boardCategory,title,content);
        if(dtos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * 게시글 단건 조회하는 API 입니다.
     *
     * @param id 게시글 식별자
     * @return 게시글 ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardFindOneResponseDto> findBoardById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){

        User loginUser = customUserDetails != null ? customUserDetails.getUser() : null;

        BoardFindOneResponseDto dto = boardService.findBoardById(id, loginUser);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


     /**
     * 게시글 업데이트하는 API 입니다.
     *
     * @param id 게시글 식별자
     * @param dto 게시글 업데이트 dto
     * @param customUserDetails 인증 정보
     * @return Void ResponseEntity
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){

        boardService.updateBoard(customUserDetails.getUser(), id, dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 게시글 삭제하는 API 입니다.
     *
     * @param id 게시글 식별자
     * @param customUserDetails 인증 정보
     * @return Void ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){

        boardService.deleteBoard(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
