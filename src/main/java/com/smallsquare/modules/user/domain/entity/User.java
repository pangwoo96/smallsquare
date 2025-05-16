package com.smallsquare.modules.user.domain.entity;

import com.smallsquare.common.util.BaseTimeEntity;
import com.smallsquare.modules.user.domain.enums.IsActive;
import com.smallsquare.modules.user.domain.enums.Role;
import com.smallsquare.modules.user.domain.vo.*;
import com.smallsquare.modules.user.web.dto.request.UserSignupReqDto;
import com.smallsquare.modules.user.web.dto.request.UserUpdateReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Embedded
    private Username username;

    @Embedded
    private Password password;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Email email;

    @Embedded
    private Name name;

    private IsActive isActive;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static User of(Username username, Password password, Email email, Nickname nickname, Name name) {
        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email(email)
                .name(name)
                .isActive(IsActive.ACTIVE)
                .role(Role.USER)
                .build();
    }

    public void updateInfo(UserUpdateReqDto reqDto,Username newUsername, Email newEmail, Nickname newNickName, Name newName) {
        username = newUsername;
        name = newName;
        email = newEmail;
        nickname = newNickName;
    }

    public void deactivate() {
        isActive = IsActive.INACTIVE;
    }

    public void updatePassword(String newRowPassword, PasswordEncoder passwordEncoder) {
        password = new Password(newRowPassword, passwordEncoder);
    }

}
