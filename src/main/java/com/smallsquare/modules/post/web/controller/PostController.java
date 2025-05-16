package com.smallsquare.modules.post.web.controller;

import com.smallsquare.modules.post.application.service.PostService;
import com.smallsquare.modules.post.web.dto.request.CreatePostReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("/")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostReqDto reqDto) {
        postService.createPost(reqDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
