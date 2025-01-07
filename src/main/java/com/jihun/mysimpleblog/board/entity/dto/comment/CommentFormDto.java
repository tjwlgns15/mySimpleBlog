package com.jihun.mysimpleblog.board.entity.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentFormDto {

    @NotNull(message = "댓글 내용은 필수입니다.")
    private String content;

    private Long parentId;
}
