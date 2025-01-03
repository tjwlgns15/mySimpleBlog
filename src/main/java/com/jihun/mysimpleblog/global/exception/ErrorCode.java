package com.jihun.mysimpleblog.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    DUPLICATE_EMAIL(400, "사용중인 이메일입니다."),
    DUPLICATE_NAME(400, "사용중인 닉네임입니다."),

    // 401 Unauthorized
    LOGIN_REQUIRED(401, "로그인이 필요합니다."),

    // 403 Forbidden
    NOT_AUTHORIZED(403, "권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND_USER(404, "회원 정보가 일치하지 않습니다."),
    NOT_FOUND_POST(404, "삭제된 게시글 입니다."),
    NOT_FOUND_COMMENT(404, "삭제된 댓글입니다."),
    CATEGORY_NOT_EXIST(404, "카테고리를 선택해 주세요."),

    // 409 Conflict
    DUPLICATE_CATEGORY_NAME(409, "존재하는 카테고리입니다.");

    private final int code;
    private final String message;
}
