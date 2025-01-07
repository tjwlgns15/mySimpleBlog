package com.jihun.mysimpleblog.board.service;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.Role;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.board.entity.Comment;
import com.jihun.mysimpleblog.board.entity.Post;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentFormDto;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentResponse;
import com.jihun.mysimpleblog.board.entity.dto.comment.CommentUpdateDto;
import com.jihun.mysimpleblog.board.entity.dto.post.PageResponse;
import com.jihun.mysimpleblog.board.repository.CommentRepository;
import com.jihun.mysimpleblog.board.repository.PostRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jihun.mysimpleblog.auth.entity.Role.ADMIN;
import static com.jihun.mysimpleblog.global.constant.GlobalConstant.COMMENT_PAGE_SIZE;
import static com.jihun.mysimpleblog.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Transactional
    public CommentResponse create(Long postId, CommentFormDto dto, CustomUserDetails userDetails) {
        User user = validateAndGetUser(userDetails);
        Post post = validateAndGetPost(postId);

        log.info("Creating new comment. postId: {}, author: {}", postId, user.getEmail());

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = validateAndGetComment(dto.getParentId());
            validateCommentBelongsToPost(parent, postId);
        }

        Comment comment = Comment.builder()
                .post(post)
                .author(user)
                .content(dto.getContent())
                .parent(parent)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created. id: {}, author: {}", savedComment.getId(), user.getEmail());

        return CommentResponse.fromEntity(savedComment);
    }

    @Transactional
    public CommentResponse update(Long postId, Long commentId, CommentUpdateDto dto, CustomUserDetails userDetails) {
        User user = validateAndGetUser(userDetails);
        Comment comment = validateAndGetComment(commentId);
        validateCommentBelongsToPost(comment, postId);
        validateAuthorization(comment, user);

        comment.update(dto.getContent());
        log.info("Comment updated. id: {}", commentId);

        return CommentResponse.fromEntity(comment);
    }

    @Transactional
    public void delete(Long postId, Long commentId, CustomUserDetails userDetails) {
        User user = validateAndGetUser(userDetails);
        Comment comment = validateAndGetComment(commentId);
        validateCommentBelongsToPost(comment, postId);
        validateAuthorization(comment, user);

        commentRepository.delete(comment);
        log.info("Comment deleted. id: {}", commentId);
    }

    public PageResponse<CommentResponse> getComments(Long postId, int page) {
        validatePostExists(postId);

        Page<Comment> comments = commentRepository.findByPostIdWithAuthor(
                postId,
                PageRequest.of(page, COMMENT_PAGE_SIZE)
        );

        return new PageResponse<>(comments.map(CommentResponse::fromEntity));
    }

    private User validateAndGetUser(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(LOGIN_REQUIRED);
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    private Post validateAndGetPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(NOT_FOUND_POST);
        }
    }

    private Comment validateAndGetComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));
    }

    private void validateCommentBelongsToPost(Comment comment, Long postId) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new CustomException(NOT_FOUND_COMMENT);
        }
    }

    private void validateAuthorization(Comment comment, User user) {
        if (!comment.getAuthor().equals(user) && !user.getRole().equals(ADMIN)) {
            throw new CustomException(NOT_AUTHORIZED);
        }
    }
}
