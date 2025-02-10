package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private static final long VERIFICATION_TIME_LIMIT = 5; //5분
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;
    private final Map<String, VerificationInfo> verificationMap = new ConcurrentHashMap<>();

    public void sendVerificationEmail(String email) {
        // 이미 가입된 이메일인지 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }

        String verificationCode = generateVerificationCode();

        try {
            MimeMessage message = createEmailMessage(email, verificationCode);
            emailSender.send(message);

            // 메모리에 인증 정보 저장
            // Todo: 추후 Redis로 수정 예정
            verificationMap.put(email, new VerificationInfo(
                    verificationCode,
                    LocalDateTime.now().plusMinutes(VERIFICATION_TIME_LIMIT)
            ));
        } catch (MailException e) {
            throw new ApiException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    public boolean verifyEmail(String email, String code) {
        // 인증 정보 확인
        VerificationInfo verificationInfo = getVerificationInfo(email);

        //인증 정보가 없는 경우
        if (verificationInfo == null) {
            throw new ApiException(ErrorCode.VERIFICATION_NOT_FOUND);
        }

        // 인증 정보가 만료된 경우
        if (LocalDateTime.now().isAfter(verificationInfo.getExpiryTime())) {
            verificationMap.remove(email);
            throw new ApiException(ErrorCode.VERIFICATION_TIME_EXPIRED);
        }

        // 인증 코드 불일치
        if (!verificationInfo.getCode().equals(code)) {
            throw new ApiException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 인증 성공 시 상태 변경
        verificationInfo.markAsVerified();
        verificationMap.put(email, verificationInfo);

        // 만료된 모든 인증 정보 제거
        verificationMap.entrySet().removeIf(entry ->
                LocalDateTime.now().isAfter(entry.getValue().getExpiryTime())
        );

        return true;
    }

    public boolean isEmailVerified(String email) {
        VerificationInfo info = verificationMap.get(email);

        return info != null
                && !LocalDateTime.now().isAfter(info.getExpiryTime())
                && info.isVerified();
    }

    // Todo: 추후 Redis로 수정 예정
    private VerificationInfo getVerificationInfo(String email) {
        VerificationInfo info = verificationMap.get(email);

        if (info != null && LocalDateTime.now().isAfter(info.getExpiryTime())) {
            verificationMap.remove(email);
            return null;
        }

        return info;
    }

    private String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private MimeMessage createEmailMessage(String email, String code) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("lee24pm@gmail.com");
            helper.setTo(email);
            helper.setSubject("[GameMate] 이메일 인증");
            helper.setText(createEmailContent(code), true);

            return message;
        } catch (MessagingException e) {
            throw new ApiException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    private String createEmailContent(String code) {
        return String.format(
                "<div style='margin:20px;'>" +
                        "<h1> GameMate 이메일 인증 </h1>" +
                        "<br>" +
                        "<p>아래 인증 코드를 입력해주세요.</p>" +
                        "<br>" +
                        "<div style='font-size:130%%'>인증 코드 : <strong>%s</strong></div>" +
                        "</div>", code);
    }

    @Getter
    @RequiredArgsConstructor
    private static class VerificationInfo {
        private final String code;
        private final LocalDateTime expiryTime;
        private boolean verified = false;

        public void markAsVerified() {
            this.verified = true;
        }
    }

}
