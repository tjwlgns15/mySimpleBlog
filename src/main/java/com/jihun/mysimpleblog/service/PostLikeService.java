package com.jihun.mysimpleblog.service;

import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.dto.PostLikeDto;
import com.jihun.mysimpleblog.dto.mapper.PostLikeMapper;
import com.jihun.mysimpleblog.repository.UserRepository;
import com.jihun.mysimpleblog.domain.Post;
import com.jihun.mysimpleblog.domain.PostLike;
import com.jihun.mysimpleblog.repository.PostLikeRepository;
import com.jihun.mysimpleblog.repository.PostRepository;
import com.jihun.mysimpleblog.infra.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jihun.mysimpleblog.dto.PostLikeDto.*;
import static com.jihun.mysimpleblog.infra.exception.ErrorCode.NOT_FOUND_POST;
import static com.jihun.mysimpleblog.infra.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    private final PostLikeMapper postLikeMapper;


    public PostLikeResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER));

        boolean exists = postLikeRepository.existsByPostAndUser(post, user);
        if (exists) {
            postLikeRepository.deleteByPostAndUser(post, user);
        } else {
            PostLike like = postLikeMapper.toEntity(post, user);
            postLikeRepository.save(like);
        }

        return postLikeMapper.toResponse(post, user);
    }

    @Transactional(readOnly = true)
    public PostLikeResponse getLikeStatus(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER));

        return postLikeMapper.toResponse(post, user);
    }
}
