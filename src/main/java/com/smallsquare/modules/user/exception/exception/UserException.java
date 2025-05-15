package com.smallsquare.modules.user.exception.exception;

import com.smallsquare.modules.user.exception.errorCode.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final UserErrorCode errorCode;

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
