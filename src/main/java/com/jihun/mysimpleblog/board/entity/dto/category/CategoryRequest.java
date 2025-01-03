package com.jihun.mysimpleblog.board.entity.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryRequest {
    @NotBlank(message = "카테고리 이름은 필수입니다")
    private String name;
}