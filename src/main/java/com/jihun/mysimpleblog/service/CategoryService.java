package com.jihun.mysimpleblog.service;

import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.dto.mapper.CategoryMapper;
import com.jihun.mysimpleblog.repository.UserRepository;
import com.jihun.mysimpleblog.domain.Category;
import com.jihun.mysimpleblog.repository.CategoryRepository;
import com.jihun.mysimpleblog.repository.PostRepository;
import com.jihun.mysimpleblog.infra.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jihun.mysimpleblog.dto.CategoryDto.*;
import static com.jihun.mysimpleblog.infra.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, Long userId) {
        validateAdminUser(userId);

        if (categoryRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_CATEGORY_NAME);
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created. id: {}, name: {}",
                savedCategory.getId(), savedCategory.getName());

        return categoryMapper.toResponse(savedCategory);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));
        return categoryMapper.toResponse(category);
    }


    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, Long userId) {
        validateAdminUser(userId);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));

        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_CATEGORY_NAME);
        }

        categoryMapper.updateEntity(category, request);

        log.info("Category updated. id: {}, name: {}", id, request.getName());

        return categoryMapper.toResponse(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        validateAdminUser(userId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));

        if (postRepository.existsByCategoryId(categoryId)) {
            throw new CustomException(CATEGORY_HAS_POSTS);
        }

        categoryRepository.delete(category);
        log.info("Category deleted. categoryId: {}, name: {}", categoryId, category.getName());
    }


    private void validateAdminUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER));

        if (!user.isAdmin()) {
            throw new CustomException(NOT_AUTHORIZED);
        }
    }
}
