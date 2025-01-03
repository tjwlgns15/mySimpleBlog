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
import com.jihun.mysimpleblog.global.constant.GlobalConstant;
import com.jihun.mysimpleblog.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private User validateAndGetUser(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(LOGIN_REQUIRED);
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    private Comment validateAndGetComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));
    }

    private void validateAuthorization(Comment comment, User user) {
        if (!comment.getAuthor().equals(user) && !user.getRole().equals(ADMIN)) {
            throw new CustomException(NOT_AUTHORIZED);
        }
    }

    @Transactional
    public CommentResponse create(CommentFormDto dto, CustomUserDetails userDetails) {
        User user = validateAndGetUser(userDetails);

        if (dto.getPostId() == null) {
            throw new CustomException(NOT_FOUND_POST);
        }

        log.info("Creating new comment. postId: {}, author: {}", dto.getPostId(), user.getEmail());

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = validateAndGetComment(dto.getParentId());
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

    public PageResponse<CommentResponse> getComments(Long postId, int page) {
        Page<Comment> comments = commentRepository.findByPostIdWithAuthor(
                postId,
                PageRequest.of(page, GlobalConstant.COMMENT_PAGE_SIZE)
        );

        return new PageResponse<>(comments.map(CommentResponse::fromEntity));
    }

    @Transactional
    public CommentResponse update(Long id, CommentUpdateDto dto, CustomUserDetails userDetails) {
        User user = validateAndGetUser(userDetails);
        Comment comment = validateAndGetComment(id);
        validateAuthorization(comment, user);

        comment.update(dto.getContent());
        log.info("Comment updated. id: {}", id);

        return CommentResponse.fromEntity(comment);
    }

    @Transactional
    public void delete(Long id, CustomUserDetails userDetails) {
        User user = validateAndGetUser(userDetails);
        Comment comment = validateAndGetComment(id);
        validateAuthorization(comment, user);

        commentRepository.delete(comment);
        log.info("Comment deleted. id: {}", id);
    }
}
