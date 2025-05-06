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
    DUPLICATED_NICKNAME("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),
    PASSWORD_NOT_MATCHED("비밀번호가 일치하지 않습니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PASSWORD_MISMATCH("비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.CONFLICT),
    ACCESS_TOKEN_EXPIRED("이미 만료된 Access Token입니다.", HttpStatus.CONFLICT),
    REFRESH_TOKEN_EXPIRED("이미 만료된 Refresh Token입니다.", HttpStatus.CONFLICT),
    INACTIVE_ACCOUNT("탈퇴한 회원 계정입니다.", HttpStatus.FORBIDDEN),
    EMAIL_NOT_EXIST("존재하지 않는 이메일입니다.", HttpStatus.NOT_FOUND),
    SAME_AS_OLD_PASSWORD("이전과 동일한 비밀번호는 사용할 수 없습니다.", HttpStatus.CONFLICT),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다.", HttpStatus.CONFLICT);


    private final String message;
    private final HttpStatus status;
}
