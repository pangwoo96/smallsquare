package com.smallsquare.modules.user.web.controller;

import com.smallsquare.modules.user.infrastructure.mail.MailService;
import com.smallsquare.modules.user.web.dto.request.MailReqDto;
import com.smallsquare.modules.user.web.dto.request.UserVerifyEmailReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/find/password")
    public ResponseEntity<Void> sendPasswordMail(@Valid @RequestBody MailReqDto reqDto) {
        mailService.sendFindAndResetPassword(reqDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/sendVerifyEmail")
    public ResponseEntity<Void> sendVerifyEmail(@Valid @RequestBody MailReqDto reqDto) {
        mailService.sendVerifyEmail(reqDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody UserVerifyEmailReqDto reqDto) {
        mailService.verifyEmail(reqDto.getVerifyEmailToken());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
