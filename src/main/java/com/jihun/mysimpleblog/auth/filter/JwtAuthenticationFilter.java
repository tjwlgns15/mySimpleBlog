package com.jihun.mysimpleblog.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.jwt.JwtTokenProvider;
import com.jihun.mysimpleblog.auth.model.dto.LoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.DialectOverride;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 인증(유저 확인)을 담담하는 필터
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            ObjectMapper objectMapper
    ) throws Exception {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;

        // 로그인 URL 설정
        setFilterProcessesUrl("/api/auth/login");
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

        response.addHeader("Authorization", "Bearer " + token);

        // JWT 토큰을 쿠키에 추가
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가능하게 설정
        cookie.setSecure(true);    // HTTPS에서만 전송되도록 설정
        cookie.setPath("/");       // 모든 경로에서 접근 가능하도록 설정
        cookie.setMaxAge(3600);    // 쿠키 유효시간 1시간
        response.addCookie(cookie);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> result = new HashMap<>();
        result.put("token", token);

        objectMapper.writeValue(response.getWriter(), result);
    }
}