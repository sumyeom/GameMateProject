package com.example.gamemate.domain.notification.controller;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.service.NotificationService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 알림을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 로그인한 사용자를 SSE 에 연결합니다.
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 로그인한 사용자의 SseEmitter 를 담은 ResponseEntity
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        SseEmitter sseEmitter = notificationService.subscribe(customUserDetails.getUser());
        return new ResponseEntity<>(sseEmitter, HttpStatus.OK);
    }

    /**
     * 로그인한 사용자의 전체 알림을 조회합니다.
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 로그인한 사용자의 전체 알림을 담은 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> findAllNotification(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        List<NotificationResponseDto> notificationResponseDtoList = notificationService.findAllNotification(customUserDetails.getUser());
        return new ResponseEntity<>(notificationResponseDtoList, HttpStatus.OK);
    }

    /**
     * 단일 알림의 읽음 상태를 처리합니다.
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @param id 읽음 상태를 처리할 알림 id
     * @return 204 NO_CONTENT
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> readNotification(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long id
    ) {

        notificationService.readNotification(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
