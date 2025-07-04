package com.jihun.mysimpleblog.dto.mapper;

import com.jihun.mysimpleblog.domain.Category;
import org.springframework.stereotype.Service;

import static com.jihun.mysimpleblog.dto.CategoryDto.*;

@Service
public class CategoryMapper {

    /**
     * CategoryRequest DTO를 Category 엔티티로 변환
     */
    public Category toEntity(CategoryRequest categoryRequest) {
        if (categoryRequest == null) {
            return null;
        }

        return Category.builder()
                .name(categoryRequest.getName())
                .build();
    }

    /**
     * CategoryRequest DTO로 Category 엔티티 업데이트
     */
    public void updateEntity(Category category, CategoryRequest categoryRequest) {
        if (category == null || categoryRequest == null) {
            return;
        }

        category.updateName(categoryRequest.getName());
    }

    /**
     * Category 엔티티를 CategoryResponse DTO로 변환
     */
    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}