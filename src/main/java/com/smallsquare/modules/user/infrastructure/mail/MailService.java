package com.smallsquare.modules.user.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    public void sendResetPasswordEmail(String toEmail) {

        // 1. UUID 토큰 생성
        String token = UUID.randomUUID().toString();

        // 2. redis에 저장할 key & value 설정
        String key = "findPassword:token" + token;
        String value = toEmail;

        // 3. redis에 key & value 설정
        redisTemplate.opsForValue().set(
                key, value, Duration.ofMinutes(15)
        );

        // 4. 링크 주소를 포함한 메일 발송
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        sendEmail(toEmail, resetLink);
    }

    private void sendEmail(String toEmail, String resetLink) {
        String subject = "[스몰스퀘어] 비밀번호 재설정 링크입니다.";
        String content = "아래 링크를 클릭하여 비밀번호를 재설정하세요.\n\n" +
                resetLink + "\n\n" +
                "이 링크는 15분간만 유효합니다.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("smallsquare99@gmail.com");

        mailSender.send(message);
    }
}