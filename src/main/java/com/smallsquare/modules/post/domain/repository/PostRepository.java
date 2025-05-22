package com.smallsquare.modules.post.domain.repository;

import com.smallsquare.modules.post.domain.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository {

    Post save(Post post);


    Optional<Post> findById(Long postId);
}
