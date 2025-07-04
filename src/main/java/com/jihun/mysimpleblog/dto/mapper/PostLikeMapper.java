package com.jihun.mysimpleblog.dto.mapper;

import com.jihun.mysimpleblog.domain.Post;
import com.jihun.mysimpleblog.domain.PostLike;
import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.dto.PostLikeDto;
import com.jihun.mysimpleblog.dto.PostLikeDto.PostLikeResponse;
import org.springframework.stereotype.Service;

@Service
public class PostLikeMapper {

    /**
     * Post와 User로 PostLike 엔티티 생성
     */
    public PostLike toEntity(Post post, User user) {
        if (post == null || user == null) {
            return null;
        }

        return PostLike.builder()
                .post(post)
                .user(user)
                .build();
    }

    /**
     * Post 정보를 기반으로 PostLikeResponse DTO 생성
     * @param post 게시글 엔티티
     * @param user 현재 사용자 (좋아요 여부 확인용)
     * @return PostLikeResponse DTO
     */
    public PostLikeResponse toResponse(Post post, User user) {
        if (post == null) {
            return null;
        }

        boolean isLiked = user != null &&
                post.getLikes().stream()
                        .anyMatch(like -> like.getUser().equals(user));

        return PostLikeResponse.builder()
                .postId(post.getId())
                .likeCount(post.getLikeCount())
                .liked(isLiked)
                .build();
    }

    /**
     * Post 정보를 기반으로 PostLikeResponse DTO 생성 (사용자 정보 없음)
     * @param post 게시글 엔티티
     * @return PostLikeResponse DTO (liked는 항상 false)
     */
    public PostLikeResponse toResponse(Post post) {
        return toResponse(post, null);
    }
}
