package com.smallsquare.modules.user.domain.vo;

import com.smallsquare.modules.user.exception.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.NAME_NOT_NULL;
import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.NAME_WRONG_PATTERN;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {

    /**
     * 한글과 영어 혼용 금지, 최대 30자까지 작성 가능
     */
    private static final String KOREAN_NAME_REGEX = "^[가-힣]{1,30}$";
    private static final String ENGLISH_NAME_REGEX = "^[a-zA-Z]{1,30}$";

    private static final Pattern KOREAN_PATTERN = Pattern.compile(KOREAN_NAME_REGEX);
    private static final Pattern ENGLISH_PATTERN = Pattern.compile(ENGLISH_NAME_REGEX);

    @Column(name = "name", nullable = false)
    private String name;

    public Name(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new UserException(NAME_NOT_NULL);
        }
        if (!KOREAN_PATTERN.matcher(name).matches() && !ENGLISH_PATTERN.matcher(name).matches()) {
            throw new UserException(NAME_WRONG_PATTERN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Name name1 = (Name) o;
        return Objects.equals(name, name1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
