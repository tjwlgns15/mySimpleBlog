package com.jihun.mysimpleblog.board.controller;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentFormDto;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentResponse;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentUpdateDto;
import com.jihun.mysimpleblog.board.service.CommentService;
import com.jihun.mysimpleblog.board.service.PostService;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/new")
    public ApiResponse<CommentResponse> createComment(
            @RequestBody @Valid CommentFormDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(commentService.create(dto, userDetails));
    }

    @PutMapping("/{id}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long id,
            @RequestBody @Valid CommentUpdateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(commentService.update(id, dto, userDetails));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CommentResponse> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.delete(id, userDetails);
        return ApiResponse.success(null);
    }
}
