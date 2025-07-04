package com.jihun.mysimpleblog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostLikeDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostLikeResponse {
        private Long postId;
        private int likeCount;
        private boolean liked;
    }
}
