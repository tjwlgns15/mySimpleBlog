package com.jihun.mysimpleblog.auth.service;

import com.jihun.mysimpleblog.auth.entity.ProfileImage;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.entity.dto.ProfileUpdateRequest;
import com.jihun.mysimpleblog.auth.entity.dto.SignupRequest;
import com.jihun.mysimpleblog.auth.entity.dto.UserResponse;
import com.jihun.mysimpleblog.auth.repository.ProfileImageRepository;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final ProfileImageRepository profileImageRepository;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(DUPLICATE_EMAIL);
        }

        if (userRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_NAME);
        }

        User user = request.toEntity(passwordEncoder);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserResponse updateIntroduce(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!user.getName().equals(request.getName()) && userRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_NAME);
        }

        user.updateProfile(request.getName(), request.getIntroduction());
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 기존 프로필 이미지가 있다면 삭제
        if (user.getProfileImage() != null) {
            fileService.deleteFile(user.getProfileImage().getUrl());
            profileImageRepository.delete(user.getProfileImage());
        }

        String imageUrl = fileService.uploadFile(file);

        ProfileImage profileImage = new ProfileImage(imageUrl,user);

        profileImageRepository.save(profileImage);
        user.updateProfileImage(profileImage);

        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse findByIdWithProfileImage(Long userId) {
        User user = userRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        return UserResponse.fromEntity(user);


    }
}
