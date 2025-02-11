package com.jihun.mysimpleblog.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.jwt.JwtTokenProvider;
import com.jihun.mysimpleblog.auth.entity.dto.LoginRequest;
import com.jihun.mysimpleblog.global.entity.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jihun.mysimpleblog.auth.entity.Role.ADMIN;
import static com.jihun.mysimpleblog.global.exception.ErrorCode.LOGIN_FAILED;

// 인증(유저 확인)을 담담하는 필터
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );

            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse login request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String token = jwtTokenProvider.createToken(userDetails.getUsername(), userDetails.getUser().getRole());

        // JWT 토큰을 쿠키에 추가
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가능하게 설정
        cookie.setSecure(false);    // HTTPS에서만 전송되도록 설정
        cookie.setPath("/");       // 모든 경로에서 접근 가능하도록 설정
        cookie.setMaxAge(14400);    // 쿠키 유효시간 4시간
        response.addCookie(cookie);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Authorization", "Bearer " + token);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
//        data.put("role", userDetails.getUser().getRole().name());
        // 권한에 따른 리다이렉트 URL 추가
        data.put("redirectUrl", userDetails.getUser().getRole() == ADMIN ?
                "/admin" : "/user");

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.success(data);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> errorResponse = ApiResponse.error(LOGIN_FAILED);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}