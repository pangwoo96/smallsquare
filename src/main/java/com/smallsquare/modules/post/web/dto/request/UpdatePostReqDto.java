package com.smallsquare.modules.post.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostReqDto {

    private Long postId;

    private String title;

    private String content;

    private List<String> imageUrls;
}
