package com.jihun.mysimpleblog.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.infra.model.ApiResponse;
import com.jihun.mysimpleblog.security.userDetails.CustomUserDetails;
import com.jihun.mysimpleblog.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final AuthorizationService authorizationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 권한 정보 확인
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> data = new HashMap<>();

        // 권한에 따른 리다이렉트 URL 결정
        String redirectUrl = authorizationService.getRedirectUrlByRole(userDetails);
        data.put("redirectUrl", redirectUrl);

        // 사용자 기본 정보
        data.put("userId", userDetails.getUserId());
        data.put("username", userDetails.getUsername());
        data.put("email", userDetails.getEmail());
        data.put("authorities", userDetails.getAuthorities());
        data.put("highestRole", authorizationService.getHighestRole(userDetails));

        // 권한별 플래그
        data.put("isAdmin", authorizationService.isAdmin(userDetails));
        data.put("isManager", authorizationService.isManager(userDetails));
        data.put("isUser", authorizationService.isUser(userDetails));
        data.put("isGuest", authorizationService.isGuest(userDetails));
        data.put("hasManagementRole", authorizationService.hasManagementRole(userDetails));

        // OAuth2 사용자 여부
        data.put("isOAuth2User", userDetails.isOAuth2User());
        if (userDetails.isOAuth2User()) {
            data.put("socialProvider", userDetails.getSocialProvider().name());
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(data);
        objectMapper.writeValue(response.getWriter(), apiResponse);

        clearAuthenticationAttributes(request);

        log.info("로그인 성공 - 사용자: {}, 권한: {}, 리다이렉트: {}",
                userDetails.getUsername(), userDetails.getRole().getRoleName(), redirectUrl);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}