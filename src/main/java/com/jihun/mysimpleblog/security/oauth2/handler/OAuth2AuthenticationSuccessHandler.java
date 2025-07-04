package com.jihun.mysimpleblog.security.oauth2.handler;

import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.domain.UserRole;
import com.jihun.mysimpleblog.security.userDetails.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User user = userDetails.getUser();

            // 권한에 따른 리다이렉트 URL 결정
            String redirectUrl = determineTargetUrl(user.getRole());

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }

    }

    private String determineTargetUrl(UserRole role) {
        if (role == UserRole.ADMIN) {
            return "/admin/dashboard";
        } else {
            return "/home";
        }
    }

    private RedirectStrategy getRedirectStrategy() {
        return new DefaultRedirectStrategy();
    }

}