package com.jihun.mysimpleblog.controller.api;

import com.jihun.mysimpleblog.dto.CategoryDto.*;
import com.jihun.mysimpleblog.security.userDetails.CustomUserDetails;
import com.jihun.mysimpleblog.service.CategoryService;
import com.jihun.mysimpleblog.infra.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;


    @PostMapping("/new")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails currentUser) {
        CategoryResponse response = categoryService.createCategory(request, currentUser.getUserId());

        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> response = categoryService.getAllCategories();

        return ApiResponse.success(response);
    }

    @GetMapping("{categoryId}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable("categoryId") Long categoryId) {
        CategoryResponse response = categoryService.getCategory(categoryId);

        return ApiResponse.success(response);
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                        @RequestBody @Valid CategoryRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails currentUser) {
        CategoryResponse response = categoryService.updateCategory(categoryId, request, currentUser.getUserId());

        return ApiResponse.success(response);
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId,
                                            @AuthenticationPrincipal CustomUserDetails currentUser) {
        categoryService.deleteCategory(categoryId, currentUser.getUserId());

        return ApiResponse.success(null);
    }

}
