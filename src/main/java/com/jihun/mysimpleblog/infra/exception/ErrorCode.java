package com.jihun.mysimpleblog.infra.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    DUPLICATE_EMAIL(400, "사용중인 이메일입니다."),
    DUPLICATE_NAME(400, "사용중인 닉네임입니다."),
    CATEGORY_HAS_POSTS(400, "해당 카테고리에 게시글이 존재하여 삭제할 수 없습니다."),
    FILE_IS_EMPTY(400, "파일이 비어있습니다."),
    INVALID_FILE_TYPE(400, "이미지 파일만 업로드 가능합니다."),


    // 401 Unauthorized
    LOGIN_REQUIRED(401, "로그인이 필요합니다."),
    LOGIN_FAILED(401, "이메일 또는 비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_ACCESS(401, "접근 권한이 없습니다."),
    CREDENTIALS_EXPIRED(401, "인증이 만료되었습니다."),
    AUTHENTICATION_FAILED(401, "인증처리에 실패하였습니다."),

    // 403 Forbidden
    NOT_AUTHORIZED(403, "권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND_USER(404, "회원 정보가 일치하지 않습니다."),
    NOT_FOUND_POST(404, "게시글을 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(404, "댓글을 찾을 수 없습니다."),
    CATEGORY_NOT_EXIST(404, "카테고리를 선택해 주세요."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),

    // 409 Conflict
    DUPLICATE_CATEGORY_NAME(409, "존재하는 카테고리입니다."),

    // 500
    FILE_UPLOAD_ERROR(500, "파일 업로드에 실패했습니다."),
    FILE_DELETE_ERROR(500, "파일 삭제에 실패했습니다.");


    ;

    private final int code;
    private final String message;
}
