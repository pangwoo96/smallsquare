package com.smallsquare.modules.post.infrastructure.repository;

import com.smallsquare.modules.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long postId);

}
