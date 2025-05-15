package com.smallsquare.modules.user.domain.vo;

import com.smallsquare.modules.user.exception.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Password {

    /**
     * 8자 이상, 영문자/숫자/특수문자 포함 필수
     */
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Password 생성자에 유효성 검증 및 인코딩 된 값 대입
     *
     * @param password
     * @param encoder
     */
    public Password(String password, PasswordEncoder encoder) {
        validate(password);
        this.password = encoder.encode(password);
    }

    /**
     * 비밀번호 검증
     *
     * @param password
     */
    private void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new UserException(PASSWORD_NOT_NULL);
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new UserException(PASSWORD_WRONG_PATTERN);
        }
    }

    /**
     * 평문 비밀번호와 인코딩된 비밀번호가 일치하는지 여부 확인 (로그인 시)
     * @param rawPassword
     * @param encoder
     * @return
     */
    public boolean matchPassword(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, password);
    }

    /**
     * 변경할 비밀번호가 기존 비밀번호와 동일한지 확인
     * @param rawPassword
     * @param encoder
     * @return
     */

    public boolean isSamePassword(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.password); // this.password는 인코딩된 비번
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Password password1 = (Password) o;
        return Objects.equals(password, password1.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(password);
    }
}
