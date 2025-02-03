package com.example.gamemate.domain.board.service;

import com.example.gamemate.domain.board.dto.BoardRequestDto;
import com.example.gamemate.domain.board.dto.BoardResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindOneResponseDto;
import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.enums.ListSize;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 게시글 생성 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param dto 게시글 생성 dto
     * @return 게시글 생성 응답 ResponseDto
     */
    @Transactional
    public BoardResponseDto createBoard(User loginUser, BoardRequestDto dto) {
        // 게시글 생성
        Board newBoard = new Board(dto.getCategory(),dto.getTitle(),dto.getContent(), loginUser);
        Board createdBoard = boardRepository.save(newBoard);
        return new BoardResponseDto(
                createdBoard.getId(),
                createdBoard.getCategory(),
                createdBoard.getTitle(),
                createdBoard.getContent(),
                createdBoard.getUser().getNickname(),
                createdBoard.getCreatedAt(),
                createdBoard.getModifiedAt()
        );
    }

    /**
     * 게시판 리스트 조회 메서드입니다.
     *
     * @param page 페이지 번호 (기본값 : 0)
     * @param category 게시글 카테고리
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @return 게시글 리스트 ResponseDto List
     */
    public List<BoardFindAllResponseDto> findAllBoards(int page, BoardCategory category, String title, String content) {

        Pageable pageable = PageRequest.of(page, ListSize.BOARD_LIST_SIZE.getSize(), Sort.by(Sort.Order.desc("createdAt")));

        Page<Board> boardPage = boardRepository.searchBoardQuerydsl(category, title, content, pageable);


        return boardPage.stream()
                .map(board -> new BoardFindAllResponseDto(
                        board.getId(),
                        board.getCategory(),
                        board.getTitle(),
                        board.getCreatedAt(),
                        board.getViews()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 게시글 단건 조회 메서드입니다.
     *
     * @param id 게시글 식별자
     * @return 게시글 조회 ResponseDto
     */
    public BoardFindOneResponseDto findBoardById(Long id) {
        // 게시글 조회
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_NOT_FOUND));

        return new BoardFindOneResponseDto(findBoard);
    }

    /**
     * 게시글 업데이트 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param id 게시글 식별자
     * @param dto 게시글 업데이트 요청 Dto
     */
    @Transactional
    public void updateBoard(User loginUser, Long id, BoardRequestDto dto) {
        // 게시글 조회
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_NOT_FOUND));

        // 게시글 작성자와 로그인한 사용자 확인
        if(!findBoard.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        findBoard.updateBoard(dto.getCategory(),dto.getTitle(),dto.getContent());
        boardRepository.save(findBoard);
    }

    /**
     * 게시글 삭제 메서드입니다.
     *
     * @param loginUser 로그인한 유저
     * @param id 게시글 식별자
     */
    @Transactional
    public void deleteBoard(User loginUser, Long id) {
        //게시글 조회
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_NOT_FOUND));

        // 게시글 작성자와 로그인한 사용자 확인
        if(!findBoard.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        boardRepository.delete(findBoard);
    }
}
