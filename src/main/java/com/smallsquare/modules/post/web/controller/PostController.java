package com.smallsquare.modules.post.web.controller;

import com.smallsquare.modules.post.application.service.PostService;
import com.smallsquare.modules.post.web.dto.request.CreatePostReqDto;
import com.smallsquare.modules.post.web.dto.request.UpdatePostReqDto;
import com.smallsquare.modules.post.web.dto.response.UpdatePostResDto;
import com.smallsquare.modules.user.infrastructure.auth.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("/")
    public ResponseEntity<Void> createPost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CreatePostReqDto reqDto) {
        postService.createPost(userDetails.getUserId(), reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/")
    public ResponseEntity<UpdatePostResDto> updatePost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdatePostReqDto reqDto) {
        UpdatePostResDto resDto = postService.updatePost(userDetails.getUserId(), reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }
}
