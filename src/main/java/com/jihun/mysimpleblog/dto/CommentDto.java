package com.jihun.mysimpleblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentRequest { // CommentFormDto
        @NotNull(message = "댓글 내용은 필수입니다.")
        private String content;

        private Long parentId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private Long id;
        private String content;
        private String authorName;
        private Long authorId;
        private LocalDateTime createdAt;
        private List<CommentResponse> replies;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentUpdateRequest{ //CommentUpdateDto
        @NotBlank(message = "댓글 내용은 필수입니다")
        private String content;
    }




}
