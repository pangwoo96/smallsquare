package com.smallsquare.modules.post.domain.vo;

import com.smallsquare.modules.post.exception.errorCode.PostErrorCode;
import com.smallsquare.modules.post.exception.exception.PostException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.smallsquare.modules.post.exception.errorCode.PostErrorCode.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Content {

    private static final String CONTENT_REGEX = "^.{1,10000}$";
    private static final Pattern CONTENT_PATTERN = Pattern.compile(CONTENT_REGEX, Pattern.DOTALL);

    @Column(name = "content", nullable = false)
    private String content;

    /**
     * @ElementCollection은 값 타입 컬렉션을 위한 어노테이션
     * 여기서는 이미지 URL 문자열들을 post_id 기준으로 별도 테이블(post_images)에 저장
     * Post가 저장될 때 imageUrls도 자동으로 함께 관리
     * 이미지가 null이면 Post 테이블에만 저장되고 image_url 테이블에는 저장 X
     * imageUrls는 List<String>이고 JPA는 이걸 별도 테이블에 각 요소마다 row로 저장해준다.
     */
    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    public Content(String content, List<String> newImageUrls) {
        validate(content);
        this.content = content;
        this.imageUrls = (newImageUrls == null) ? new ArrayList<>() : newImageUrls;
    }

    private void validate(String content) {
        if (content == null) {
            throw new PostException(CONTENT_NOT_NULL);
        }
        if (!CONTENT_PATTERN.matcher(content).matches()) {
            throw new PostException(CONTENT_WRONG_PATTERN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Content content1 = (Content) o;
        return Objects.equals(content, content1.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(content);
    }
}
