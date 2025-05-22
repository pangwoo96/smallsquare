package com.smallsquare.modules.post.application.service;

import com.smallsquare.modules.post.domain.entity.Post;
import com.smallsquare.modules.post.domain.repository.PostRepository;
import com.smallsquare.modules.post.domain.vo.Content;
import com.smallsquare.modules.post.domain.vo.Title;
import com.smallsquare.modules.post.exception.errorCode.PostErrorCode;
import com.smallsquare.modules.post.exception.exception.PostException;
import com.smallsquare.modules.post.web.dto.request.CreatePostReqDto;
import com.smallsquare.modules.post.web.dto.request.UpdatePostReqDto;
import com.smallsquare.modules.post.web.dto.response.UpdatePostResDto;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.domain.repository.UserRepository;
import com.smallsquare.modules.user.exception.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smallsquare.modules.post.exception.errorCode.PostErrorCode.POST_NOT_FOUND;
import static com.smallsquare.modules.post.exception.errorCode.PostErrorCode.POST_NOT_OWNER;
import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createPost(Long userId, CreatePostReqDto reqDto) {

        // 1. User 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 2. Title VO 생성
        Title title = new Title(reqDto.getTitle());

        // 3. Content VO 생성
        Content content = new Content(reqDto.getContent(), reqDto.getImageUrls());

        // 4. Post 객체 빌드
        Post post = Post.of(title, content, user);

        // 5. Post 저장
        postRepository.save(post);
    }

    @Transactional
    public UpdatePostResDto updatePost(Long userId, UpdatePostReqDto reqDto) {

        // 1. Post 조회
        Post post = postRepository.findById(reqDto.getPostId()).orElseThrow(() -> new PostException(POST_NOT_FOUND));

        // 2. Post를 작성한 작성자가 맞는지 확인
        if (!post.isOwnedBy(userId)) {
            throw new PostException(POST_NOT_OWNER);
        }

        // 3.Title 객체 생성
        Title title = new Title(reqDto.getTitle());

        // 4. Content 객체 생성
        Content content = new Content(reqDto.getContent(), reqDto.getImageUrls());

        // 5. Post의 updatePost() 호출
        post.updatePost(title, content);

        // 6. 반환
        UpdatePostResDto resDto = UpdatePostResDto.builder()
                .postId(post.getId())
                .title(post.getTitle().getTitle())
                .content(post.getContent().getContent())
                .imageUrls(post.getContent().getImageUrls())
                .build();

        return resDto;

    }
}
