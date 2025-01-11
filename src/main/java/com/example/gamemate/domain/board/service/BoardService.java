package com.example.gamemate.domain.board.service;

import com.example.gamemate.domain.board.dto.BoardRequestDto;
import com.example.gamemate.domain.board.dto.BoardResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindOneResponseDto;
import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.enums.ListSize;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.comment.dto.CommentFindResponseDto;
import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.comment.repository.CommentRepository;
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

import static com.example.gamemate.global.constant.ErrorCode.BOARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    /**
     * 게시글 생성 메서드
     * @param dto
     * @return
     */
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto dto) {
        // 게시글 생성
        Board newBoard = new Board(dto.getCategory(),dto.getTitle(),dto.getContent());
        Board createdBoard = boardRepository.save(newBoard);
        return new BoardResponseDto(
                createdBoard.getBoardId(),
                createdBoard.getCategory(),
                createdBoard.getTitle(),
                createdBoard.getContent()
        );
    }

    /**
     * 게시판 리스트 조회 메서드
     * @param page
     * @param category
     * @return
     */
    public List<BoardFindAllResponseDto> findAllBoards(int page, BoardCategory category, String title, String content) {

        Pageable pageable = PageRequest.of(page, ListSize.LIST_SIZE.getSize(), Sort.by(Sort.Order.desc("createdAt")));

        Page<Board> boardPage = boardRepository.searchBoardQuerydsl(category, title, content, pageable);


        return boardPage.stream()
                .map(board -> new BoardFindAllResponseDto(
                        board.getBoardId(),
                        board.getCategory(),
                        board.getTitle(),
                        board.getCreatedAt(),
                        board.getViews()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 게시글 단건 조회 메서드
     * @param page
     * @param id
     * @return
     */
    public BoardFindOneResponseDto findBoardById(int page, Long id) {
        // page는 댓글 페이지네이션을 위해 필요
        Pageable pageable = PageRequest.of(page, ListSize.LIST_SIZE.getSize(), Sort.by(Sort.Order.asc("createdAt")));
        // 게시글 조회
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(()->new ApiException(BOARD_NOT_FOUND));

        // 댓글 조회
        Page<Comment> comments = commentRepository.findByBoard(findBoard,pageable);

        List<CommentFindResponseDto> commentDtos = comments.stream()
                        .map(comment-> new CommentFindResponseDto(
                                comment.getCommentId(),
                                comment.getContent(),
                                comment.getCreatedAt(),
                                comment.getModifiedAt()
                        ))
                        .collect(Collectors.toList());

        return new BoardFindOneResponseDto(
                findBoard.getBoardId(),
                findBoard.getCategory(),
                findBoard.getTitle(),
                findBoard.getContent(),
                findBoard.getCreatedAt(),
                findBoard.getModifiedAt(),
                commentDtos
        );
    }

    /**
     * 게시글 업데이트 메서드
     * @param id
     * @param dto
     * @return
     */
    @Transactional
    public void updateBoard(Long id, BoardRequestDto dto) {
        // 게시글 조회
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(()->new ApiException(BOARD_NOT_FOUND));

        findBoard.updateBoard(dto.getCategory(),dto.getTitle(),dto.getContent());
        Board updatedBoard = boardRepository.save(findBoard);
    }

    /**
     * 게시글 삭제 메서드
     * @param id
     */
    @Transactional
    public void deleteBoard(Long id) {
        //게시글 조회
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(()->new ApiException(BOARD_NOT_FOUND));

        boardRepository.delete(findBoard);
    }
}
