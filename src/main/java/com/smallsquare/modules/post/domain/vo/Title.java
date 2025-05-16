package com.smallsquare.modules.post.domain.vo;

import com.smallsquare.modules.post.exception.errorCode.PostErrorCode;
import com.smallsquare.modules.post.exception.exception.PostException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.smallsquare.modules.post.exception.errorCode.PostErrorCode.TITLE_NOT_NULL;
import static com.smallsquare.modules.post.exception.errorCode.PostErrorCode.TITLE_WRONG_PATTERN;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Title {

    private static final String TITLE_REGEX = "^.{1,30}$";
    private static final Pattern TITLE_PATTERN = Pattern.compile(TITLE_REGEX);

    @Column(name = "title", nullable = false)
    private String title;

    public Title(String title) {
        validate(title);
        this.title = title;
    }

    private void validate(String title) {
        if (title == null) {
            throw new PostException(TITLE_NOT_NULL);
        }
        if (!TITLE_PATTERN.matcher(title).matches()) {
            throw new PostException(TITLE_WRONG_PATTERN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Title title1 = (Title) o;
        return Objects.equals(title, title1.title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title);
    }
}
