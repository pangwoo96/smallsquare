package com.smallsquare.modules.user.web.dto.response;

import com.smallsquare.modules.user.domain.vo.Email;
import com.smallsquare.modules.user.domain.vo.Name;
import com.smallsquare.modules.user.domain.vo.Nickname;
import com.smallsquare.modules.user.domain.vo.Username;
import lombok.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResDto {

    public UserInfoResDto(Username username, Nickname nickname, Email email, Name name) {
        this.username = username.getUsername();
        this.nickname = nickname.getNickname();
        this.email = email.getEmail();
        this.name = name.getName();
    }

    private String username;

    private String nickname;

    private String name;

    private String email;
}
