package com.jihun.mysimpleblog.board.controller;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.board.entity.dto.post.*;
import com.jihun.mysimpleblog.board.service.PostService;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/new")
    public ApiResponse<PostResponse> createPost(@RequestBody @Valid PostFormDto dto,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(postService.create(dto, userDetails));
    }

    @PutMapping("/{id}")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long id,
                                                @RequestBody @Valid PostUpdateDto dto,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setId(id);
        return ApiResponse.success(postService.update(dto, userDetails));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.delete(id, userDetails);
        return ApiResponse.success(null);
    }

}
