package com.smallsquare.modules.user.web.controller;

import com.smallsquare.modules.user.infrastructure.mail.MailService;
import com.smallsquare.modules.user.infrastructure.redis.RedisService;
import com.smallsquare.modules.user.web.dto.request.MailReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/find/password")
    public ResponseEntity<Void> sendTestMail(@Valid @RequestBody MailReqDto reqDto) {
        mailService.sendResetPasswordEmail(reqDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
