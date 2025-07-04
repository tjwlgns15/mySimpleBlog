package com.jihun.mysimpleblog.service;

import com.jihun.mysimpleblog.domain.ProfileImage;
import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.domain.UserRole;
import com.jihun.mysimpleblog.dto.UserDto.ProfileUpdateRequest;
import com.jihun.mysimpleblog.dto.UserDto.SignupRequest;
import com.jihun.mysimpleblog.dto.UserDto.UserResponse;
import com.jihun.mysimpleblog.dto.mapper.UserMapper;
import com.jihun.mysimpleblog.infra.exception.CustomException;
import com.jihun.mysimpleblog.infra.utils.FileService;
import com.jihun.mysimpleblog.repository.ProfileImageRepository;
import com.jihun.mysimpleblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.jihun.mysimpleblog.infra.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final ProfileImageRepository profileImageRepository;

    private final UserMapper userMapper;


    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(DUPLICATE_EMAIL);
        }

        if (userRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_NAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.createUser(
                request.getUsername(),
                encodedPassword,
                request.getName(),
                UserRole.GUEST
        );

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse findByIdWithProfileImage(Long userId) {
        if (userId == null) {
            throw new CustomException(LOGIN_REQUIRED);
        }

        User user = userRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateIntroduce(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        userMapper.updateEntity(user, request);

        return userMapper.toResponse(user);
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

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }


}
