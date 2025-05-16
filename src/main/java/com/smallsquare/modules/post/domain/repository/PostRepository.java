package com.smallsquare.modules.post.domain.repository;

import com.smallsquare.modules.post.domain.entity.Post;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository {

    Post save(Post post);


}
