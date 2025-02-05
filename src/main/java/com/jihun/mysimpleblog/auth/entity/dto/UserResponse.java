package com.jihun.mysimpleblog.auth.entity.dto;

import com.jihun.mysimpleblog.auth.entity.Provider;
import com.jihun.mysimpleblog.auth.entity.Role;
import com.jihun.mysimpleblog.auth.entity.User;
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
    private String introduction;
    private String profileImageUrl;
    private Provider provider;
    private Role role;

    // Entity -> DTO 변환 메서드
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getIntroduction(),
                user.getProfileImage() != null ? user.getProfileImage().getUrl() : null,
                user.getProvider(),
                user.getRole()
        );
    }
}