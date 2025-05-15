package com.smallsquare.modules.user.exception.errorCode;

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
    PASSWORD_NOT_MATCHED("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PASSWORD_MISMATCH("비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED("이미 만료된 Access Token입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("이미 만료된 Refresh Token입니다.", HttpStatus.UNAUTHORIZED),
    INACTIVE_ACCOUNT("탈퇴한 회원 계정입니다.", HttpStatus.FORBIDDEN),
    EMAIL_NOT_EXIST("존재하지 않는 이메일입니다.", HttpStatus.NOT_FOUND),
    SAME_AS_OLD_PASSWORD("이전과 동일한 비밀번호는 사용할 수 없습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다.", HttpStatus.FORBIDDEN),
    EXPIRED_REFRESH_TOKEN("폐기된 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_NULL("비밀번호는 필수 값입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_WRONG_PATTERN("비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야합니다.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_NULL("이메일은 필수 값입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_WRONG_PATTERN("이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_NULL("회원 아이디는 필수 값입니다.", HttpStatus.BAD_REQUEST),
    USERNAME_WRONG_PATTERN("회원 아이디는 최소 6자이상 20자 이내이며 영문자와 숫자만 사용해야합니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_NOT_NULL("회원 닉네임은 필수 값입니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_WRONG_PATTERN("회원 닉네임은 최소 3글자, 최대 15글자까지 가능하며 특수문자는 사용할 수 없습니다.", HttpStatus.BAD_REQUEST),
    NAME_NOT_NULL("회원 이름은 필수 값입니다.", HttpStatus.BAD_REQUEST),
    NAME_WRONG_PATTERN("올바르지 않은 이름 형식입니다.", HttpStatus.BAD_REQUEST);



    private final String message;
    private final HttpStatus status;
}
