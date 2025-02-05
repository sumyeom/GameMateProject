package com.example.gamemate.domain.board.service;

import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoardViewService {

    private final BoardRepository boardRepository;
    private final String VIEW_COUNT_KEY = "board:view:";
    private final String VIEW_RANKING_KEY = "board:ranking:";
    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

    public BoardViewService(
            BoardRepository boardRepository,
            @Qualifier("viewCountRedisTemplate")StringRedisTemplate redisTemplate,
            HttpServletRequest request) {
        this.boardRepository = boardRepository;
        this.redisTemplate = redisTemplate;
        this.request = request;
    }

    /**
     * 조회수 높은 게시글 조회 하는 메서드입니다.
     *
     * @param boardCategory 카테고리 종류
     * @return 게시글 조회 List
     */
    public List<BoardFindAllResponseDto> findTopBoards(BoardCategory boardCategory) {
        List<Board> top5Boards = getTop5Boards();

        List<BoardFindAllResponseDto> result = new ArrayList<>();

        for(Board board : top5Boards) {
            int redisViewCount = getViewCount(board.getId());
            result.add(new BoardFindAllResponseDto(
                    board.getId(),
                    board.getCategory(),
                    board.getTitle(),
                    board.getCreatedAt(),
                    redisViewCount
            ));
        }

        return result;
    }

    /**
     * 조회수 증가시키는 메서드입니다.
     *
     * @param boardId 게시글 식별자
     */
    @Transactional
    public void increaseViewCount(Long boardId, Long userId) {
        String uniqueKey;

        if (userId != null) {
            // 회원 : userId 기반으로 조회 제한
            uniqueKey = VIEW_COUNT_KEY + boardId + ":" + userId;
        } else {
            // 비회원
            String ipAddress = getClientIp();
            //String hashedIp = hashIpAddress(ipAddress);
            uniqueKey = VIEW_COUNT_KEY + boardId + ":" + ipAddress;
        }

        if (Boolean.FALSE.equals(redisTemplate.hasKey(uniqueKey))) {
            redisTemplate.opsForValue().set(uniqueKey, "1", Duration.ofHours(1));
            redisTemplate.opsForValue().increment(VIEW_COUNT_KEY + boardId);
            redisTemplate.opsForZSet().incrementScore(VIEW_RANKING_KEY, String.valueOf(boardId),1);
        }
    }

    /**
     * 조회수 가져오는 메서드 입니다.
     *
     * @param boardId 게시글 식별자
     * @return 조회수
     */
    public int getViewCount(Long boardId){

        String key = VIEW_COUNT_KEY + boardId;
        String count = redisTemplate.opsForValue().get(key);

        if(count != null){
            return Integer.parseInt(count);
        }

        // Redis에 값이 없으면 DB에서 조회 후 Redis 에 반영
        Board board = boardRepository.findById(boardId)
                .orElseThrow(()->new ApiException(ErrorCode.BOARD_NOT_FOUND));

        int dbViewCount = board.getViews();

        // Redis 에 저장(초기화)
        redisTemplate.opsForValue().set(key, String.valueOf(dbViewCount));

        return dbViewCount;
    }

    /**
     * 클라이언트 IP 가져오는 메서드입니다.(프록시)
     *
     * @return ip 주소
     */
    private String getClientIp(){

        String ip = request.getHeader("x-forwarded-for");
        if( ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if( ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if( ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     * 매일 00시 정각 시간마다 동기화
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void syncRedisToDb(){
        log.info("Redis 조회수 데이터 DB로 동기화");

        // 조회수 기반으로 DB에 업데이트
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY + "*")
                .stream()
                .filter(key -> key.split(":").length == 3) // board:view:{boardId} 형식만 남김
                .collect(Collectors.toSet());
        if(!keys.isEmpty()){
            List<Board> updatedBoards = new ArrayList<>();
            for(String key : keys){
                Long boardId = Long.parseLong(key.split(":")[2]);
                int viewCount = getViewCount(boardId);
                if(viewCount > 0){
                    Board findBoard = boardRepository.findById(boardId)
                            .orElseThrow(()->new ApiException(ErrorCode.BOARD_NOT_FOUND));
                    int boardViewCount = findBoard.getViews();
                    findBoard.updateViewCount( viewCount);
                    updatedBoards.add(findBoard);

                    // redis 값을 유지(db 값 반영 후 덮어쓰기)
                    redisTemplate.opsForValue().set(VIEW_COUNT_KEY + boardId, String.valueOf(viewCount));
                }
            }
            // 업데이트
            boardRepository.saveAll(updatedBoards);
            log.info("업데이트 완료");
        }

    }

    /**
     * 조회수 높은 5개의 게시글 조회
     *
     * @return 조회수 높은 게시글 리스트
     */
    public List<Board> getTop5Boards(){
        Set<String> topBoardIds = redisTemplate.opsForZSet().reverseRange(VIEW_RANKING_KEY, 0, 4);

        if(topBoardIds == null || topBoardIds.isEmpty()){
            return boardRepository.findTop5ByOrderByCreatedAtDesc();
        }

        List<Long> boardIds = topBoardIds.stream().map(Long::parseLong).toList();

        // DB 에서 해당 게시글 조회(조회수 순으로 정렬)
        List<Board> boards = boardRepository.findByIdIn(boardIds);

        // 조회수는 redis 값으로 최신화
        boards.forEach(board -> {
            int redisViewCount = getViewCount(board.getId());
            board.updateViewCount(redisViewCount);
        });

        boards.sort(Comparator.comparing(Board::getViews).reversed()
                .thenComparing(Board::getCreatedAt, Comparator.reverseOrder()));

        return boards;
    }
}
