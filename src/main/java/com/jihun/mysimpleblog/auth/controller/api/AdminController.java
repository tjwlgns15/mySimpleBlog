package com.jihun.mysimpleblog.auth.controller.api;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.dto.ProfileUpdateRequest;
import com.jihun.mysimpleblog.auth.entity.dto.SignupRequest;
import com.jihun.mysimpleblog.auth.entity.dto.UserResponse;
import com.jihun.mysimpleblog.auth.service.UserService;
import com.jihun.mysimpleblog.board.entity.dto.post.PostResponse;
import com.jihun.mysimpleblog.board.service.PostService;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.LOGIN_REQUIRED;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;


    @GetMapping("/users")
    public ApiResponse<List<UserResponse>> getUsers() {
        List<UserResponse> allUsers = userService.findAll();
        return ApiResponse.success(allUsers);
    }

    @GetMapping("/posts")
    public ApiResponse<List<PostResponse>> getPosts() {
        List<PostResponse> allPosts = postService.findAll();
        return ApiResponse.success(allPosts);
    }


}

