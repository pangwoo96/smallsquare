package com.smallsquare.common.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum UserErrorCode {

    // 에러 메시지와 상태 코드를 관리
    DUPLICATED_USERNAME("이미 존재하는 아이디입니다.", HttpStatus.CONFLICT),
    DUPLICATED_EMAIL("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATED_NICKNAME("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;
}
