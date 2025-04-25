package com.smallsquare.common.exception.exception;

import com.smallsquare.common.exception.errorCode.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final UserErrorCode errorCode;

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
