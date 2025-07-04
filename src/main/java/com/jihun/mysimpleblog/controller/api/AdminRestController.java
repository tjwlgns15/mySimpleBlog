package com.jihun.mysimpleblog.controller.api;

import com.jihun.mysimpleblog.dto.PostDto.PostResponse;
import com.jihun.mysimpleblog.service.UserService;
import com.jihun.mysimpleblog.service.PostService;
import com.jihun.mysimpleblog.infra.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jihun.mysimpleblog.dto.UserDto.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;
    private final PostService postService;


    @GetMapping("/users")
    public ApiResponse<List<UserResponse>> getUsers() {
        List<UserResponse> allUsers = userService.findAll();
        return ApiResponse.success(allUsers);
    }

    @GetMapping("/posts")
    public ApiResponse<List<PostResponse>> getPosts() {
        List<PostResponse> allPosts = postService.findAllPosts();
        return ApiResponse.success(allPosts);
    }
}

