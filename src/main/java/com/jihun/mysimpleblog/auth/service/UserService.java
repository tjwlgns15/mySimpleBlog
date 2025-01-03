package com.jihun.mysimpleblog.auth.service;

import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.entity.dto.SignupRequest;
import com.jihun.mysimpleblog.auth.entity.dto.UserResponse;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.jihun.mysimpleblog.global.exception.ErrorCode.DUPLICATE_NAME;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
