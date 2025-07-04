package com.jihun.mysimpleblog.dto.mapper;

import com.jihun.mysimpleblog.domain.Comment;
import com.jihun.mysimpleblog.domain.Post;
import com.jihun.mysimpleblog.domain.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jihun.mysimpleblog.dto.CommentDto.*;

@Service
public class CommentMapper {

    /**
     * CommentRequest DTO를 Comment 엔티티로 변환
     */
    public Comment toEntity(CommentRequest commentRequest, Post post, User author, Comment parent) {
        if (commentRequest == null || post == null || author == null) {
            return null;
        }

        return Comment.builder()
                .post(post)
                .author(author)
                .content(commentRequest.getContent())
                .parent(parent)
                .build();
    }

    /**
     * CommentUpdateRequest DTO로 Comment 엔티티 업데이트
     */
    public void updateEntity(Comment comment, CommentUpdateRequest commentUpdateRequest) {
        if (comment == null || commentUpdateRequest == null) {
            return;
        }

        comment.updateComment(commentUpdateRequest.getContent());
    }

    /**
     * Comment 엔티티를 CommentResponse DTO로 변환 (대댓글 포함)
     */
    public CommentResponse toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        List<CommentResponse> replies = comment.getChildren() != null
                ? comment.getChildren().stream()
                .map(this::toResponse)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getAuthor().getName())
                .authorId(comment.getAuthor().getId())
                .createdAt(comment.getCreatedAt())
                .replies(replies)
                .build();
    }

    /**
     * Comment 엔티티를 CommentResponse DTO로 변환 (대댓글 제외)
     * 대댓글을 별도로 조회하거나 성능 최적화가 필요한 경우 사용
     */
    public CommentResponse toResponseWithoutReplies(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getAuthor().getName())
                .authorId(comment.getAuthor().getId())
                .createdAt(comment.getCreatedAt())
                .replies(Collections.emptyList())
                .build();
    }

    /**
     * Comment 엔티티 리스트를 CommentResponse DTO 리스트로 변환
     */
    public List<CommentResponse> toResponseList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}