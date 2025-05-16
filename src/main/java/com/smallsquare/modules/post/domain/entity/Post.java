package com.smallsquare.modules.post.domain.entity;

import com.smallsquare.modules.comment.domain.entity.Comment;
import com.smallsquare.common.util.BaseTimeEntity;
import com.smallsquare.modules.post.domain.enums.PostStatus;
import com.smallsquare.modules.post.domain.vo.Content;
import com.smallsquare.modules.post.domain.vo.Title;
import com.smallsquare.modules.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    private Long commentCount;

    private Long likeCount;

    private Long dislikeCount;

    private Long viewCount;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public static Post of (Title title, Content content, User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .commentCount(0L)
                .likeCount(0L)
                .dislikeCount(0L)
                .viewCount(0L)
                .postStatus(PostStatus.ACTIVE)
                .build();
    }

}
