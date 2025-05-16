package com.smallsquare.modules.post.application.service;

import com.smallsquare.modules.post.domain.entity.Post;
import com.smallsquare.modules.post.domain.repository.PostRepository;
import com.smallsquare.modules.post.domain.vo.Content;
import com.smallsquare.modules.post.domain.vo.Title;
import com.smallsquare.modules.post.web.dto.request.CreatePostReqDto;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.domain.repository.UserRepository;
import com.smallsquare.modules.user.exception.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createPost(CreatePostReqDto reqDto) {

        // 1. User 조회
        User user = userRepository.findById(reqDto.getUserId()).orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 2. Title VO 생성
        Title title = new Title(reqDto.getTitle());

        // 3. Content VO 생성
        Content content = new Content(reqDto.getContent(), reqDto.getImageUrls());

        // 4. Post 객체 빌드
        Post post = Post.of(title, content, user);

        // 5. Post 저장
        postRepository.save(post);
    }
}
