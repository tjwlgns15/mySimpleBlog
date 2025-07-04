package com.jihun.mysimpleblog.service;

import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.dto.mapper.CommentMapper;
import com.jihun.mysimpleblog.repository.UserRepository;
import com.jihun.mysimpleblog.domain.Comment;
import com.jihun.mysimpleblog.domain.Post;
import com.jihun.mysimpleblog.repository.CommentRepository;
import com.jihun.mysimpleblog.repository.PostRepository;
import com.jihun.mysimpleblog.infra.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jihun.mysimpleblog.domain.UserRole.ADMIN;
import static com.jihun.mysimpleblog.dto.CommentDto.*;
import static com.jihun.mysimpleblog.infra.exception.ErrorCode.*;
import static com.jihun.mysimpleblog.infra.model.CustomPageDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final CommentMapper commentMapper;


    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        log.info("Creating new comment. postId: {}, author: {}", postId, user.getUsername());

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));
            validateCommentBelongsToPost(parent, postId);
        }

        Comment comment = commentMapper.toEntity(request, post, user, parent);
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment created. id: {}, author: {}", savedComment.getId(), user.getUsername());

        return commentMapper.toResponse(savedComment);
    }

    public CustomPageResponse<CommentResponse> getComments(Long postId, CustomPageRequest customPageRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        Pageable pageRequest = customPageRequest.toPageRequest();

        Page<CommentResponse> commentResponses = commentRepository.findByPostIdWithAuthor(post.getId(), pageRequest)
                .map(commentMapper::toResponse);

        return CustomPageResponse.fromPage(commentResponses);
    }


    @Transactional
    public CommentResponse updateComment(Long postId, Long commentId, CommentUpdateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));

        validateAuthorization(comment, user);
        validateCommentBelongsToPost(comment, postId);

        commentMapper.updateEntity(comment, request);

        log.info("Comment updated. id: {}", commentId);

        return commentMapper.toResponse(comment);
    }

    @Transactional
    public void delete(Long postId, Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));

        validateAuthorization(comment, user);
        validateCommentBelongsToPost(comment, postId);

        commentRepository.delete(comment);
        log.info("Comment deleted. id: {}", commentId);
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
