package com.jihun.mysimpleblog.board.entity.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class PostFormDto {
    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @NotNull(message = "카테고리를 선택해주세요")
    private Long categoryId;
}
