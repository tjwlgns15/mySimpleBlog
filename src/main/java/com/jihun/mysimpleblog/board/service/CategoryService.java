package com.jihun.mysimpleblog.board.service;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.Role;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.board.entity.Category;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryRequest;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryResponse;
import com.jihun.mysimpleblog.board.repository.CategoryRepository;
import com.jihun.mysimpleblog.board.repository.PostRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    @Transactional
    public CategoryResponse create(CategoryRequest request, CustomUserDetails userDetails) {
        validateAdminUser(userDetails);

        if (categoryRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_CATEGORY_NAME);
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created. id: {}, name: {}",
                savedCategory.getId(), savedCategory.getName());

        return CategoryResponse.fromEntity(savedCategory);
    }

    private void validateAdminUser(CustomUserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER));

        if (!user.getRole().equals(Role.ADMIN)) {
            throw new CustomException(NOT_AUTHORIZED);
        }
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                        () -> new CustomException(CATEGORY_NOT_EXIST));
        return CategoryResponse.fromEntity(category);
    }


    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request, CustomUserDetails userDetails) {
        validateAdminUser(userDetails);

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CustomException(CATEGORY_NOT_EXIST));

        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_CATEGORY_NAME);
        }

        category.update(request.getName());
        log.info("Category updated. id: {}, name: {}", id, request.getName());

        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public void delete(Long id, CustomUserDetails userDetails) {
        validateAdminUser(userDetails);

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CustomException(CATEGORY_NOT_EXIST));

        if (postRepository.existsByCategoryId(id)) {
            throw new CustomException(CATEGORY_HAS_POSTS);
        }

        categoryRepository.delete(category);
        log.info("Category deleted. id: {}, name: {}", id, category.getName());
    }
}
