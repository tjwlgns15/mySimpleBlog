package com.jihun.mysimpleblog.board.controller;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.Role;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryRequest;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryResponse;
import com.jihun.mysimpleblog.board.service.CategoryService;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import com.jihun.mysimpleblog.global.entity.BaseTimeEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/new")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.success(categoryService.create(request, userDetails));
    }

}
