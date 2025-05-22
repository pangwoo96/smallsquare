package com.smallsquare.modules.post.infrastructure.repository;

import com.smallsquare.modules.post.domain.entity.Post;
import com.smallsquare.modules.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final JpaPostRepository jpaPostRepository;

    @Override
    public Post save(Post post) {
        return jpaPostRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return jpaPostRepository.findById(postId);
    }
}
