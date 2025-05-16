package com.smallsquare.modules.post.infrastructure.repository;

import com.smallsquare.modules.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPostRepository extends JpaRepository<Post, Long> {

}
