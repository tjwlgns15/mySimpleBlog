package com.jihun.mysimpleblog.controller.api;

import com.jihun.mysimpleblog.dto.UserDto.*;
import com.jihun.mysimpleblog.security.userDetails.CustomUserDetails;
import com.jihun.mysimpleblog.service.UserService;
import com.jihun.mysimpleblog.infra.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@RequestBody SignupRequest request) {
        UserResponse response = userService.signup(request);

        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> myPage(@AuthenticationPrincipal CustomUserDetails currentUser) {
        UserResponse response = userService.findByIdWithProfileImage(currentUser.getUserId());

        return ApiResponse.success(response);
    }

    @PutMapping("/introduce")
    public ApiResponse<UserResponse> updateIntroduce(@RequestBody ProfileUpdateRequest request,
                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        UserResponse response = userService.updateIntroduce(currentUser.getUserId(), request);

        return ApiResponse.success(response);
    }

    @PostMapping("/profile-image")
    public ApiResponse<UserResponse> updateProfileImage(@RequestParam("file") MultipartFile file,
                                                        @AuthenticationPrincipal CustomUserDetails currentUser) {
        UserResponse response = userService.updateProfileImage(currentUser.getUserId(), file);

        return ApiResponse.success(response);
    }
}

