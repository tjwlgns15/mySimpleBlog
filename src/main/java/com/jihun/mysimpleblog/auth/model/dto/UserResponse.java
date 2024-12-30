package com.jihun.mysimpleblog.auth.model.dto;

import com.jihun.mysimpleblog.auth.model.Provider;
import com.jihun.mysimpleblog.auth.model.Role;
import com.jihun.mysimpleblog.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private Provider provider;
    private Role role;

    // Entity -> DTO 변환 메서드
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProvider(),
                user.getRole()
        );
    }
}