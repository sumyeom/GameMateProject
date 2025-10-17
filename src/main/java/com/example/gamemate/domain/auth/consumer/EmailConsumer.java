package com.example.gamemate.domain.auth.consumer;

import com.example.gamemate.domain.auth.dto.EamilVerifyRequestDto;
import com.example.gamemate.domain.auth.service.EmailService;
import com.example.gamemate.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {
    private final EmailService emailService;

    /**
     * 이메일 발송 Consumer
     */
    @RabbitListener(
            queues = RabbitMQConfig.EMAIL_QUEUE,
            containerFactory = "emailListenerContainerFactory"
    )
    public void consumeEmailMessage(EamilVerifyRequestDto emailMessage){
        try {
            log.info("Processing email: {}", emailMessage.getEmail());

            // 실제 이메일 발송
            emailService.sendEmailDirectly(
                    emailMessage.getEmail(),
                    emailMessage.getCode()
            );

            log.info("Email sent successfully: {}", emailMessage.getEmail());

        } catch (Exception e) {
            log.error("Failed to send email: {}", emailMessage.getEmail(), e);

            // 예외 발생 시 자동으로 재시도 → DLQ 이동
            throw e;
        }
    }

    /**
     * 실패한 이메일 처리 (DLQ)
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_DLQ)
    public void handleFailedEmail(EamilVerifyRequestDto emailMessage) {
        log.error("Email permanently failed: {}", emailMessage.getEmail());

        // TODO:
        // 1. DB에 저장 (failed_emails 테이블)
        // 2. 관리자에게 알림
        // 3. 슬랙/디스코드 알림
        // 4. 수동 재발송 기능
    }
}
