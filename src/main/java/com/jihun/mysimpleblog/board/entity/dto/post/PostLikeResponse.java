package com.jihun.mysimpleblog.board.entity.dto.post;

import com.jihun.mysimpleblog.board.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResponse {
    private Long postId;
    private int likeCount;
    private boolean liked;

    @Builder
    public static PostLikeResponse fromEntity(Long postId, int likeCount, boolean liked) {
        return new PostLikeResponse(postId, likeCount, liked);
    }
}
