package com.smallsquare.modules.user.domain.vo;

import com.smallsquare.modules.user.exception.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.EMAIL_WRONG_PATTERN;
import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.EMAIL_NOT_NULL;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    /**
     * 이메일 형식 필수
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Column(name = "email", nullable = false)
    private String email;

    public Email(String email) {
        validate(email);
        this.email = email;
    }

    private void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new UserException(EMAIL_NOT_NULL);
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new UserException(EMAIL_WRONG_PATTERN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Email email1 = (Email) o;
        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
