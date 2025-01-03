package com.jihun.mysimpleblog.board.service;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.Role;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.board.entity.Category;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryRequest;
import com.jihun.mysimpleblog.board.entity.dto.category.CategoryResponse;
import com.jihun.mysimpleblog.board.repository.CategoryRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest request, CustomUserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // 관리자만 카테고리 생성 가능
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new CustomException(NOT_AUTHORIZED);
        }

        // 이름 중복 검사
        if (categoryRepository.existsByName(request.getName())) {
            throw new CustomException(DUPLICATE_CATEGORY_NAME);
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created. id: {}, name: {}", savedCategory.getId(), savedCategory.getName());

        return CategoryResponse.fromEntity(savedCategory);
    }
}
