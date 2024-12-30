package com.jihun.mysimpleblog.auth.model.dto;

import com.jihun.mysimpleblog.auth.model.Provider;
import com.jihun.mysimpleblog.auth.model.Role;
import com.jihun.mysimpleblog.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String name;


    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .name(this.name)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();
    }

}
