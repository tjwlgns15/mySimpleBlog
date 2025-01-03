package com.jihun.mysimpleblog.board.controller;

import com.jihun.mysimpleblog.board.entity.dto.comment.CommentResponse;
import com.jihun.mysimpleblog.board.entity.dto.post.PageResponse;
import com.jihun.mysimpleblog.board.entity.dto.post.PostResponse;
import com.jihun.mysimpleblog.board.entity.dto.post.PostSearchDto;
import com.jihun.mysimpleblog.board.service.CommentService;
import com.jihun.mysimpleblog.board.service.PostService;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardViewController {
    private final PostService postService;
    private final CommentService commentService;

//    @GetMapping("")
//    public String boards(Model model) {
//        return "board/boards";
//    }

    @GetMapping
    public ApiResponse<PageResponse<PostResponse>> searchPosts(
            @ModelAttribute PostSearchDto searchDto,
            @RequestParam(defaultValue = "0") int page) {
        Page<PostResponse> postPage = postService.searchPosts(searchDto, page);
        return ApiResponse.success(new PageResponse<>(postPage));
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<PageResponse<CommentResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.success(commentService.getComments(postId, page));
    }
}
