package com.jihun.mysimpleblog.auth.controller.api;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.entity.dto.ProfileUpdateRequest;
import com.jihun.mysimpleblog.auth.entity.dto.SignupRequest;
import com.jihun.mysimpleblog.auth.entity.dto.UserResponse;
import com.jihun.mysimpleblog.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        if (userDetails != null) {
            User user = userDetails.getUser();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            response.put("provider", user.getProvider());
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/introduce")
    public ResponseEntity<UserResponse> updateIntroduce(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateIntroduce(userDetails.getUser().getId(), request));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<UserResponse> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateProfileImage(userDetails.getUser().getId(), file));
    }
}

