package com.jihun.mysimpleblog.domain;

import lombok.Getter;

@Getter
public enum SocialProvider {
    LOCAL("로컬 계정"),
    GOOGLE("구글"),
    GITHUB("깃허브"),
    KAKAO("카카오"),
    NAVER("네이버");

    private final String displayName;

    SocialProvider(String displayName) {
        this.displayName = displayName;
    }

}
