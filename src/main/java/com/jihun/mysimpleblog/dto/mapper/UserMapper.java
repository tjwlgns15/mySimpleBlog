package com.jihun.mysimpleblog.dto.mapper;

import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.domain.UserRole;
import com.jihun.mysimpleblog.dto.UserDto;
import org.springframework.stereotype.Service;

import static com.jihun.mysimpleblog.dto.UserDto.*;

@Service
public class UserMapper {

    /**
     * ProfileUpdateRequest DTO로 User 엔티티 업데이트
     */
    public void updateEntity(User user, ProfileUpdateRequest updateRequest) {
        if (user == null || updateRequest == null) {
            return;
        }

        user.updateProfile(
                updateRequest.getName(),
                updateRequest.getIntroduction()
        );
    }

    /**
     * User 엔티티를 UserResponse DTO로 변환
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .introduction(user.getIntroduction())
                .profileImageUrl(extractProfileImageUrl(user))
                .provider(user.getSocialProvider())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();
    }

    /**
     * User 엔티티에서 프로필 이미지 URL 추출
     */
    private String extractProfileImageUrl(User user) {
        if (user.getProfileImage() == null) {
            return null;
        }
        return user.getProfileImage().getUrl();
    }
}