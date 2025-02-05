package com.jihun.mysimpleblog.board.controller.api;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryRequest;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryResponse;
import com.jihun.mysimpleblog.board.service.CategoryService;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @PostMapping("/new")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(categoryService.create(request, userDetails));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id,
                                                        @RequestBody @Valid CategoryRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(categoryService.update(id, request, userDetails));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        categoryService.delete(id, userDetails);
        return ApiResponse.success(null);
    }

}
