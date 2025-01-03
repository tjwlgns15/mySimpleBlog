package com.jihun.mysimpleblog.board.entity.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentFormDto {
    private Long postId;

    @NotNull(message = "댓글 내용은 필수입니다.")
    private String content;

    private Long parentId;
}
