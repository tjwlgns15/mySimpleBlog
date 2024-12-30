package com.jihun.mysimpleblog.auth.service;

import com.jihun.mysimpleblog.auth.model.Provider;
import com.jihun.mysimpleblog.auth.model.Role;
import com.jihun.mysimpleblog.auth.model.User;
import com.jihun.mysimpleblog.auth.model.dto.SignupRequest;
import com.jihun.mysimpleblog.auth.model.dto.UserResponse;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = request.toEntity(passwordEncoder);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
