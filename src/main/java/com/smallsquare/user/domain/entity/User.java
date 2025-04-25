package com.smallsquare.user.domain.entity;

import com.smallsquare.common.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;

@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

}
