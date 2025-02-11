package com.jihun.mysimpleblog.board.controller.api;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentFormDto;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentResponse;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentUpdateDto;
import com.jihun.mysimpleblog.board.entity.dto.post.*;
import com.jihun.mysimpleblog.board.service.CommentService;
import com.jihun.mysimpleblog.board.service.PostLikeService;
import com.jihun.mysimpleblog.board.service.PostService;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final PostLikeService postLikeService;
    private final CommentService commentService;

    @GetMapping
    public ApiResponse<PageResponse<PostResponse>> searchPosts(@ModelAttribute PostSearchDto searchDto,
                                                               @RequestParam(defaultValue = "0") int page) {
        Page<PostResponse> postPage = postService.searchPosts(searchDto, page);
        return ApiResponse.success(new PageResponse<>(postPage));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long id,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        return ApiResponse.success(postService.getPost(id, request, response));
    }

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

    @GetMapping("/my-posts")
    public ApiResponse<PageResponse<PostResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page) {
        Page<PostResponse> postPage = postService.findAllByAuthorId(userDetails.getUser().getId(), page);
        return ApiResponse.success(new PageResponse<>(postPage));
    }

    /**
     * 좋아요
     */
    @PostMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> toggleLike(@PathVariable Long postId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(postLikeService.toggleLike(postId, userDetails.getId()));
    }

    @GetMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> getLikeStatus(@PathVariable Long postId,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(postLikeService.getLikeStatus(postId, userDetails.getId()));
    }

    /**
     * 댓글
     */
    @GetMapping("/{postId}/comments")
    public ApiResponse<PageResponse<CommentResponse>> getComments(@PathVariable Long postId,
                                                                  @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.success(commentService.getComments(postId, page));
    }

    @PostMapping("/{postId}/comments/new")
    public ApiResponse<CommentResponse> createComment(@PathVariable Long postId,
                                                      @RequestBody @Valid CommentFormDto dto,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(commentService.create(postId, dto, userDetails));
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable Long postId,
                                                      @PathVariable Long commentId,
                                                      @RequestBody @Valid CommentUpdateDto dto,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(commentService.update(postId, commentId, dto, userDetails));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.delete(postId, commentId, userDetails);
        return ApiResponse.success(null);
    }


}
