package com.smallsquare.modules.user.infrastructure.mail;

import com.smallsquare.modules.user.infrastructure.redis.RedisService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private final RedisService redisService;

    public void sendFindAndResetPassword(String toEmail) {

        // 1. UUID 토큰 생성
        String token = UUID.randomUUID().toString();

        // 2. redis에 저장할 key & value 설정
        String key = "findPassword:token:" + token;
        String value = toEmail;

        // 3. redis에 key & value 설정
        redisTemplate.opsForValue().set(
                key, value, Duration.ofMinutes(15)
        );

        // 4. 링크 주소를 포함한 메일 발송
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        sendFindAndResetEmailText(toEmail, resetLink);
    }

    public void sendVerifyEmail(String toEmail) {

        // 1. UUID 토큰 생성
        String token = UUID.randomUUID().toString();

        // 2. redis에 저장할 key & value 설정
        String key = "verifyEmail:token:" + token;
        String value = toEmail;

        // 3. redis에 key & value 설정
        redisTemplate.opsForValue().set(
                key, value, Duration.ofMinutes(15)
        );

        // 4. 이메일 발송
        String resetLink = "http://localhost:8080/verify-email?token=" + token;
        sendVerifyEmailText(toEmail, resetLink);
    }

    public void verifyEmail (String token) {

        // 1. redis key 설정
        String key = "verifyEmail:token:" + token;

        // 2. redis에서 해당 token 값 조회
        String email = redisService.get(key);

        // 3. 인증이 완료되면
        String verifiedKey = "verifyEmail:email:" + email;

        // 4. Redis에 새로운 키와 값으로 데이터를 저장 -> 회원 가입 로직에서 true인지 판별
        redisTemplate.opsForValue().set(
                verifiedKey, "true", Duration.ofMinutes(15)
        );

        redisTemplate.delete(key);

    }

    private void sendFindAndResetEmailText(String toEmail, String resetLink) {
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

    private void sendVerifyEmailText(String toEmail, String resetLink) {
        String subject = "[스몰스퀘어] 이메일 인증 링크입니다.";
        String content = "아래 링크를 클릭하여 이메일 인증을 완료하세요.\n\n" +
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