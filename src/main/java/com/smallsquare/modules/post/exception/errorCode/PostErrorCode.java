package com.smallsquare.modules.post.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PostErrorCode {

    // 에러 메시지와 상태 코드를 관리
    TITLE_NOT_NULL("제목은 필수 값입니다.", HttpStatus.BAD_REQUEST),
    TITLE_WRONG_PATTERN("제목은 30자만 가능합니다.", HttpStatus.BAD_REQUEST),
    CONTENT_NOT_NULL("내용은 필수 값입니다.", HttpStatus.BAD_REQUEST),
    CONTENT_WRONG_PATTERN("내용은 1000자 이하만 가능합니다.", HttpStatus.BAD_REQUEST);



    private final String message;
    private final HttpStatus status;
}
