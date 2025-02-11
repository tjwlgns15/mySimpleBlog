package com.jihun.mysimpleblog.auth.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.LOGIN_REQUIRED;
import static com.jihun.mysimpleblog.global.exception.ErrorCode.NOT_AUTHORIZED;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();

        // API 요청이면 JSON 응답
        if (requestURI.startsWith("/api/")) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(LOGIN_REQUIRED)));
        }
        // 페이지 요청이면 로그인 페이지로 리다이렉트
        else {
            response.sendRedirect("/auth/login");
        }
    }
}
