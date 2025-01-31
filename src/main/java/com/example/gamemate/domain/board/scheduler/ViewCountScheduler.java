package com.example.gamemate.domain.board.scheduler;

import com.example.gamemate.domain.board.dto.BoardResponseDto;
import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.global.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountedCompleter;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final BoardRepository boardRepository;
    private final RedisService redisService;

    // 1시간마다 Redis 조회수를 DB에 반영
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    @Scheduled(fixedRate = 180000)
    public void syncViewCounts(){
        List<Board> boards = boardRepository.findAll();

        for(Board board : boards){
            int viewCount = redisService.getViewCount(board.getId());
            if(viewCount > 0){
                board.updateViewCount(viewCount);
                boardRepository.save(board);
                redisService.transferViewCountToDB(board.getId(), viewCount);
            }
        }
    }
}
