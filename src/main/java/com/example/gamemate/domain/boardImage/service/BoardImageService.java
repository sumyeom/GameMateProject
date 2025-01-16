package com.example.gamemate.domain.boardImage.service;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.boardImage.entity.BoardImage;
import com.example.gamemate.domain.boardImage.repository.BoardImageRepository;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardImageService {

    private final S3Service s3Service;
    private final BoardImageRepository boardImageRepository;
    private final BoardRepository boardRepository;

    /**
     * 이미지 업로드 메서드
     * @param loginUser
     * @param boardId
     * @param image
     * @throws IOException
     */
    @Transactional
    public void createBoardImage(User loginUser, Long boardId, MultipartFile image) throws IOException {
        // 게시글 조회
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(()-> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        // 게시글 작성자와 로그인한 유저 확인
        if(!findBoard.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        //업로드 된 이미지 파일 주소
        String publicUrl = s3Service.uploadFile(image);
        //BoardImage 테이블에 담을 변수 생성
        BoardImage boardImage = new BoardImage(image.getOriginalFilename(), image.getContentType(), publicUrl, image.getSize(),findBoard);

        //BoardImage 테이블에 저장
        boardImageRepository.save(boardImage);
    }

    /**
     * 이미지 업데이트 메서드
     * @param loginUser
     * @param id
     * @param image
     * @throws IOException
     */
    @Transactional
    public void updateBoardImage(User loginUser, Long id, MultipartFile image) throws IOException {
        // 이미지 조회
        BoardImage findBoardImage = boardImageRepository.findById(id)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_IMAGE_NOT_FOUND));

        // 이미지 업로드 유저와 로그인 유저 확인
        if(!findBoardImage.getBoard().getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        //업로드 된 이미지 파일 주소
        String publicUrl = s3Service.uploadFile(image);
        //BoardImage 테이블에 담을 변수 생성
        findBoardImage.updateBoardImage(image.getOriginalFilename(), image.getContentType(), publicUrl, image.getSize());

        //BoardImage 테이블에 저장
        boardImageRepository.save(findBoardImage);

        try{
            // S3 에서 이미지 삭제
            s3Service.deleteFile(findBoardImage.getFilePath());
        } catch(Exception e){
            log.error("파일 업로드 에러 발생 : {}",e.getMessage());
        }

    }


    /**
     * 이미지 삭제 메서드
     * @param loginUser
     * @param id
     */
    @Transactional
    public void deleteImage(User loginUser, Long id) {
        // 이미지 조회
        BoardImage findBoardImage = boardImageRepository.findById(id)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_IMAGE_NOT_FOUND));

        // 이미지 업로드 유저와 로그인 유저 확인
        if(!findBoardImage.getBoard().getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // S3 에서 이미지 삭제
        s3Service.deleteFile(findBoardImage.getFilePath());

        // 이미지 삭제
        boardImageRepository.delete(findBoardImage);
    }


}
