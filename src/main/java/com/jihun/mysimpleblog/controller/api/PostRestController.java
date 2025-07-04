package com.jihun.mysimpleblog.controller.api;

import com.jihun.mysimpleblog.dto.CommentDto.CommentRequest;
import com.jihun.mysimpleblog.dto.CommentDto.CommentResponse;
import com.jihun.mysimpleblog.dto.CommentDto.CommentUpdateRequest;
import com.jihun.mysimpleblog.infra.model.CustomPageDto.CustomPageRequest;
import com.jihun.mysimpleblog.infra.model.CustomPageDto.CustomPageResponse;
import com.jihun.mysimpleblog.security.userDetails.CustomUserDetails;
import com.jihun.mysimpleblog.service.CommentService;
import com.jihun.mysimpleblog.service.PostLikeService;
import com.jihun.mysimpleblog.service.PostService;
import com.jihun.mysimpleblog.infra.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.jihun.mysimpleblog.dto.PostDto.*;
import static com.jihun.mysimpleblog.dto.PostLikeDto.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;


    @PostMapping("/new")
    public ApiResponse<PostResponse> createPost(@RequestBody @Valid PostRequest request,
                                                @AuthenticationPrincipal CustomUserDetails currentUser) {
        PostResponse response = postService.createPost(request, currentUser.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<CustomPageResponse<PostResponse>> searchPosts(@ModelAttribute PostSearchForm request,
                                                                     CustomPageRequest customPageRequest) {
        CustomPageResponse<PostResponse> response = postService.searchPosts(request, customPageRequest);
        return ApiResponse.success(response);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId,
                                             HttpServletRequest httpRequest,
                                             HttpServletResponse httpResponse) {
        PostResponse response = postService.getPost(postId, httpRequest, httpResponse);
        return ApiResponse.success(response);
    }


    @PutMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long postId,
                                                @RequestBody @Valid PostUpdateRequest request,
                                                @AuthenticationPrincipal CustomUserDetails currentUser) {
        PostResponse response = postService.updatePost(postId, request, currentUser.getUserId());
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId,
                                        @AuthenticationPrincipal CustomUserDetails currentUser) {
        postService.deletePost(postId, currentUser.getUserId());
        return ApiResponse.success(null);
    }

    @GetMapping("/my-posts")
    public ApiResponse<CustomPageResponse<PostResponse>> getMyPosts(CustomPageRequest customPageRequest,
                                                                    @AuthenticationPrincipal CustomUserDetails currentUser) {
        CustomPageResponse<PostResponse> response = postService.findAllByAuthorId(customPageRequest, currentUser.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 좋아요
     */
    @PostMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> toggleLike(@PathVariable Long postId,
                                                    @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ApiResponse.success(postLikeService.toggleLike(postId, currentUser.getUserId()));
    }

    @GetMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> getLikeStatus(@PathVariable Long postId,
                                                       @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ApiResponse.success(postLikeService.getLikeStatus(postId, currentUser.getUserId()));
    }

    /**
     * 댓글
     */
    @PostMapping("/{postId}/comments/new")
    public ApiResponse<CommentResponse> createComment(@PathVariable Long postId,
                                                      @RequestBody @Valid CommentRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        CommentResponse response = commentService.createComment(postId, request, currentUser.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping("/{postId}/comments")
    public ApiResponse<CustomPageResponse<CommentResponse>> getComments(@PathVariable Long postId,
                                                                        CustomPageRequest customPageRequest) {
        CustomPageResponse<CommentResponse> response = commentService.getComments(postId, customPageRequest);
        return ApiResponse.success(response);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable Long postId,
                                                      @PathVariable Long commentId,
                                                      @RequestBody @Valid CommentUpdateRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        CommentResponse response = commentService.updateComment(postId, commentId, request, currentUser.getUserId());
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        commentService.delete(postId, commentId, currentUser.getUserId());
        return ApiResponse.success(null);
    }


}
