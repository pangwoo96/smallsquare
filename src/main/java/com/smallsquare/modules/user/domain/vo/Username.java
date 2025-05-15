package com.smallsquare.modules.user.domain.vo;

import com.smallsquare.modules.user.exception.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.USERNAME_NOT_NULL;
import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.USERNAME_WRONG_PATTERN;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {

    /**
     * 영문자와 숫자만 허용, 길이 6~20자
     */
    private static final String USERNAME_REGEX = "^[A-Za-z\\d]{6,20}$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    @Column(name = "username", nullable = false)
    private String username;

    public Username(String username) {
        validate(username);
        this.username = username;
    }

    private void validate(String username) {
        if (username == null || username.isBlank()) {
            throw new UserException(USERNAME_NOT_NULL);
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new UserException(USERNAME_WRONG_PATTERN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Username username1 = (Username) o;
        return Objects.equals(username, username1.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
