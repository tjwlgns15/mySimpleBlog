package com.jihun.mysimpleblog.auth.controller.api;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.entity.dto.ProfileUpdateRequest;
import com.jihun.mysimpleblog.auth.entity.dto.SignupRequest;
import com.jihun.mysimpleblog.auth.entity.dto.UserResponse;
import com.jihun.mysimpleblog.auth.service.UserService;
import com.jihun.mysimpleblog.board.entity.dto.post.PostResponse;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.LOGIN_REQUIRED;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success(userService.signup(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> myPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(userService.findByIdWithProfileImage(userDetails));
    }

    @PutMapping("/introduce")
    public ApiResponse<UserResponse> updateIntroduce(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.success(userService.updateIntroduce(userDetails.getUser().getId(), request));
    }

    @PostMapping("/profile-image")
    public ApiResponse<UserResponse> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(userService.updateProfileImage(userDetails.getUser().getId(), file));
    }
}

