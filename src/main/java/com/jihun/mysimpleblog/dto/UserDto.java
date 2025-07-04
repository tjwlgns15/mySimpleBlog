package com.jihun.mysimpleblog.dto;

import com.jihun.mysimpleblog.domain.SocialProvider;
import com.jihun.mysimpleblog.domain.UserRole;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

public class UserDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRequest {
        private String username;
        private String password;
        private String name;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String username;
        private String name;
        private String introduction;
        private String profileImageUrl;
        private SocialProvider provider;
        private LocalDateTime createdAt;
        private UserRole role;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUpdateRequest {
        private String name;
        private String introduction;
    }
}
