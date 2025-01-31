package com.example.gamemate.global.redis.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    //private final StringRedisTemplate redisTemplate;
    private final String VIEW_COUNT_KEY = "board:view:";
    private final RedisTemplate<String, String> redisTemplate;
    private final HttpServletRequest request;

    /**
     * 조회수 증가하는 메서드입니다.
     *
     * @param boardId 게시글 식별자
     */
    public void increaseViewCount(Long boardId, Long userId) {
        String uniqueKey;

        if (userId != null) {
            // 회원 : userId 기반으로 조회 제한
            uniqueKey = VIEW_COUNT_KEY + boardId + ":" + userId;
        } else {
            // 비회원
            String ipAddress = getClientIp();
            uniqueKey = VIEW_COUNT_KEY + boardId + ":" + ipAddress;
        }

        if (Boolean.FALSE.equals(redisTemplate.hasKey(uniqueKey))) {
            redisTemplate.opsForValue().set(uniqueKey, "1", Duration.ofHours(1));
            redisTemplate.opsForValue().increment(VIEW_COUNT_KEY + boardId);
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
        return count == null ? 0 : Integer.parseInt(count);
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
     * 조회수를 DB에 반영 후 Redis 에서 삭제하는 메서드입니다.
     *
     * @param boardId 게시글 식별자
     * @param count 조회수
     */
    public void transferViewCountToDB(Long boardId, int count){
        redisTemplate.delete(VIEW_COUNT_KEY + boardId);
    }
}
