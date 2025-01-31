package com.example.gamemate.domain.notification.controller;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.service.NotificationService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

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
}
