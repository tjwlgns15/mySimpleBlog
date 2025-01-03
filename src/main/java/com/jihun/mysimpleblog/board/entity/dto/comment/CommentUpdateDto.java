package com.jihun.mysimpleblog.board.entity.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateDto {
    @NotBlank(message = "댓글 내용은 필수입니다")
    private String content;
}