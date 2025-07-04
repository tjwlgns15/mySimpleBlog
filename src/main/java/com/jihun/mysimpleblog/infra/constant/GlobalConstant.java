package com.jihun.mysimpleblog.infra.constant;

public class GlobalConstant {
    // 클래스 인스턴스화 방지
    private GlobalConstant() {
        throw new IllegalStateException("Utility class");
    }

    // 페이징 관련 상수
    public static final int POST_PAGE_SIZE = 10;
    public static final int COMMENT_PAGE_SIZE = 10;
}
