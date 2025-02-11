package com.jihun.mysimpleblog.auth.jwt.filter;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.jwt.JwtTokenProvider;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JwtAuthorizationFilter.java - 인가(권한 검증)를 처리하는 필터
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Authorization 헤더 또는 쿠키에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 토큰이 만료되었다면 쿠키 삭제
        if (token != null && !jwtTokenProvider.validateToken(token)) {
            // 만료된 토큰의 쿠키 삭제
            Cookie cookie = new Cookie("jwt", null);
            cookie.setMaxAge(0);  // 즉시 만료
            cookie.setPath("/");
            response.addCookie(cookie);

            // 인증 정보 제거
            SecurityContextHolder.clearContext();
        }
        // 토큰 유효성 검증
        else if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰에서 사용자 정보 추출
            Claims claims = jwtTokenProvider.parseClaims(token);
            String email = claims.getSubject();

            // 사용자 정보 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 보안 컨텍스트에 인증 정보 설정
            CustomUserDetails principal = new CustomUserDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal, "", principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // 1. Authorization 헤더에서 토큰 추출 시도
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 쿠키에서 토큰 추출 시도
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}