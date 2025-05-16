package com.smallsquare.modules.post.exception.exception;

import com.smallsquare.modules.post.exception.errorCode.PostErrorCode;
import lombok.Getter;

@Getter
public class PostException extends RuntimeException {

    private final PostErrorCode errorCode;

    public PostException(PostErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
