package com.example.gamemate.domain.notification.listener;

import com.example.gamemate.domain.notification.repository.EmitterRepository;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationRabbitListener {
    private final EmitterRepository emitterRepository;


}
