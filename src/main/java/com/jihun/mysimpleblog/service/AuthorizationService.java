package com.jihun.mysimpleblog.service;

import com.jihun.mysimpleblog.domain.UserRole;
import com.jihun.mysimpleblog.security.userDetails.CustomUserDetails;
import org.springframework.stereotype.Service;

import static com.jihun.mysimpleblog.domain.UserRole.GUEST;

@Service
public class AuthorizationService {
    /**
     * 사용자 권한에 따른 리다이렉트 URL 반환
     */
    public String getRedirectUrlByRole(CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "/home";
        }

        UserRole role = userDetails.getRole();

        return switch (role) {
            case ADMIN -> "/admin/dashboard";
            case MANAGER -> "/manager/dashboard";
            case USER, GUEST -> "/home";
        };
    }

    /**
     * 최고 권한 반환
     */
    public String getHighestRole(CustomUserDetails userDetails) {
        if (userDetails == null) {
            return UserRole.GUEST.getRoleName();
        }
        return userDetails.getRole().getRoleName();
    }

    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails != null && userDetails.getRole() == UserRole.ADMIN;
    }

    /**
     * 매니저 권한 확인
     */
    public boolean isManager(CustomUserDetails userDetails) {
        return userDetails != null && userDetails.getRole() == UserRole.MANAGER;
    }

    /**
     * 일반 사용자 권한 확인
     */
    public boolean isUser(CustomUserDetails userDetails) {
        return userDetails != null && userDetails.getRole() == UserRole.USER;
    }

    /**
     * 게스트 권한 확인
     */
    public boolean isGuest(CustomUserDetails userDetails) {
        return userDetails != null && userDetails.getRole() == UserRole.GUEST;
    }

    /**
     * 특정 권한 이상인지 확인
     */
    public boolean hasRoleOrHigher(CustomUserDetails userDetails, UserRole requiredRole) {
        if (userDetails == null) {
            return false;
        }
        return userDetails.getRole().isHigherThan(requiredRole) || userDetails.getRole() == requiredRole;
    }

    /**
     * 관리 권한 확인 (ADMIN 또는 MANAGER)
     */
    public boolean hasManagementRole(CustomUserDetails userDetails) {
        return isAdmin(userDetails) || isManager(userDetails);
    }
}

