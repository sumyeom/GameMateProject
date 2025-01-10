package com.example.gamemate.domain.board.controller;

import com.example.gamemate.domain.board.dto.BoardRequestDto;
import com.example.gamemate.domain.board.dto.BoardResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindOneResponseDto;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    /**
     *  게시글 생성 API
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @Valid @RequestBody BoardRequestDto dto
    ){

        BoardResponseDto responseDto = boardService.createBoard(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 게시글 조회
     * @return
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
     * 게시글 단건 조회 API
     * @param page
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardFindOneResponseDto> findBoardById(
            @RequestParam(required = false, defaultValue = "0") int page,
            @PathVariable Long id
    ){

        BoardFindOneResponseDto dto = boardService.findBoardById(page,id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    /**
     * 게시글 업데이트 API
     * @param id
     * @param dto
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequestDto dto
    ){

        boardService.updateBoard(id, dto);
        return new ResponseEntity<>("업데이트 되었습니다.", HttpStatus.NO_CONTENT);
    }

    /**
     * 게시글 삭제 API
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(
            @PathVariable Long id
    ){

        boardService.deleteBoard(id);
        return new ResponseEntity<>("삭제 되었습니다", HttpStatus.NO_CONTENT);
    }
}
