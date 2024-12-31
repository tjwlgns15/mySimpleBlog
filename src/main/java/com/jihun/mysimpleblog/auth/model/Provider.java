package com.jihun.mysimpleblog.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    LOCAL,      // 일반 회원가입
    GOOGLE,     // 구글 로그인
    NAVER,      // 네이버 로그인
    KAKAO       // 카카오 로그인
}
