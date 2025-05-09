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

    /**
     * 비밀번호 찾기 이메일 발송
     * @param reqDto
     * @return
     */
    @PostMapping("/find/password")
    public ResponseEntity<Void> sendPasswordMail(@Valid @RequestBody MailReqDto reqDto) {
        mailService.sendFindAndResetPassword(reqDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 비밀번호 검증 용 이메일 발송(회원가입 시)
     * @param reqDto
     * @return
     */
    @PostMapping("/sendVerifyEmail")
    public ResponseEntity<Void> sendVerifyEmail(@Valid @RequestBody MailReqDto reqDto) {
        mailService.sendVerifyEmail(reqDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 비밀번호 검증 (회원가입 시) -> 인증상태 유지하고 최종 회원가입 때 재확인
     * @param reqDto
     * @return
     */
    @PostMapping("/verifyEmail")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody UserVerifyEmailReqDto reqDto) {
        mailService.verifyEmail(reqDto.getVerifyEmailToken());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
