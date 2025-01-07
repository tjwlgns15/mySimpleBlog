package com.jihun.mysimpleblog.board.service;

import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.board.entity.Post;
import com.jihun.mysimpleblog.board.entity.PostLike;
import com.jihun.mysimpleblog.board.entity.dto.post.PostLikeResponse;
import com.jihun.mysimpleblog.board.repository.PostLikeRepository;
import com.jihun.mysimpleblog.board.repository.PostRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.NOT_FOUND_POST;
import static com.jihun.mysimpleblog.global.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;


    public PostLikeResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER));

        boolean exists = postLikeRepository.existsByPostAndUser(post, user);
        if (exists) {
            postLikeRepository.deleteByPostAndUser(post, user);
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(like);
        }
        int likeCount = postLikeRepository.countByPostId(postId);

        return PostLikeResponse.fromEntity(postId, likeCount, !exists);
    }

    @Transactional(readOnly = true)
    public PostLikeResponse getLikeStatus(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER));

        boolean liked = postLikeRepository.existsByPostAndUser(post, user);
        int likeCount = postLikeRepository.countByPostId(postId);

        return PostLikeResponse.fromEntity(postId, likeCount, liked);
    }
}
