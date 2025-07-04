package com.jihun.mysimpleblog.dto.mapper;

import com.jihun.mysimpleblog.domain.Category;
import com.jihun.mysimpleblog.domain.Post;
import com.jihun.mysimpleblog.domain.User;
import org.springframework.stereotype.Service;

import static com.jihun.mysimpleblog.dto.PostDto.*;

@Service
public class PostMapper {
    /**
     * PostRequest DTO를 Post 엔티티로 변환
     */
    public Post toEntity(PostRequest postRequest, User author, Category category) {
        if (postRequest == null || author == null || category == null) {
            return null;
        }

        return Post.builder()
                .author(author)
                .category(category)
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();
    }

    /**
     * PostUpdateRequest DTO로 Post 엔티티 업데이트
     */
    public void updateEntity(Post post, PostUpdateRequest postUpdateRequest, Category category) {
        if (post == null || postUpdateRequest == null || category == null) {
            return;
        }

        post.updatePost(category, postUpdateRequest.getTitle(), postUpdateRequest.getContent());
    }

    /**
     * Post 엔티티를 PostResponse DTO로 변환
     */
    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(post.getAuthor().getName())
                .categoryName(post.getCategory().getName())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }


}
