package com.jihun.mysimpleblog.board.entity.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostUpdateDto extends PostFormDto {
    @NotNull(message = "게시글 ID")
    private Long id;
}
