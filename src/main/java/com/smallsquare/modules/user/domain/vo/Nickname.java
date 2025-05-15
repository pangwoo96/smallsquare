package com.smallsquare.modules.user.domain.vo;

import com.smallsquare.modules.user.exception.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.NICKNAME_NOT_NULL;
import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.NICKNAME_WRONG_PATTERN;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {

    /**
     * 최소 3글자 이상, 15글자 이내
     */
    private static final String NICKNAME_REGEX = "^[A-Za-z0-9]{3,15}$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);

    @Column(name = "nickname", nullable = false)
    private String nickname;

    public Nickname(String nickname) {
        validate(nickname);
        this.nickname = nickname;
    }

    private void validate(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new UserException(NICKNAME_NOT_NULL);
        }
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new UserException(NICKNAME_WRONG_PATTERN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Nickname nickname1 = (Nickname) o;
        return Objects.equals(nickname, nickname1.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nickname);
    }
}
