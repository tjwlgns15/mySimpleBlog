package com.jihun.mysimpleblog.board.entity.dto.comment;

import com.jihun.mysimpleblog.board.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String authorName;
    private Long authorId;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;

    public static CommentResponse fromEntity(Comment comment) {
        if (comment == null) {
            return null;
        }

        List<CommentResponse> childResponses;
        if (comment.getChildren() != null) {
            childResponses = comment.getChildren().stream()
                    .map(CommentResponse::fromEntity)
                    .toList();
        } else {
            childResponses = new ArrayList<>();
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getAuthor().getName())
                .authorId(comment.getAuthor().getId())
                .createdAt(comment.getCreatedAt())
                .replies(childResponses)
                .build();
    }
}
