package com.jihun.mysimpleblog.security.oauth2.userinfo.impl;

import com.jihun.mysimpleblog.domain.SocialProvider;
import com.jihun.mysimpleblog.security.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        Map<String, Object> response = getResponse();
        if (response == null || response.get("id") == null) {
            log.error("네이버 사용자 ID를 찾을 수 없습니다. attributes: {}", attributes);
            throw new IllegalArgumentException("네이버 사용자 ID를 찾을 수 없습니다.");
        }
        return response.get("id").toString();
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    public String getUsername() {
        Map<String, Object> response = getResponse();
        if (response == null || response.get("email") == null) {
            log.error("네이버 사용자 이메일을 찾을 수 없습니다. attributes: {}", attributes);
            throw new IllegalArgumentException("네이버 사용자 이메일을 찾을 수 없습니다.");
        }
        return response.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> response = getResponse();
        if (response == null) {
            log.error("네이버 사용자 정보를 찾을 수 없습니다. attributes: {}", attributes);
            return "네이버 사용자"; // 기본값
        }

        // 이름이 없으면 닉네임 사용, 그것도 없으면 기본값
        String name = (String) response.get("name");
        if (name == null || name.trim().isEmpty()) {
            name = (String) response.get("nickname");
        }
        if (name == null || name.trim().isEmpty()) {
            name = "네이버 사용자";
        }

        return name;
    }

    @Override
    public String getProfileImageUrl() {
        Map<String, Object> response = getResponse();
        if (response != null && response.get("profile_image") != null) {
            return response.get("profile_image").toString();
        }
        return null;
    }

    /**
     * 네이버 API 응답에서 실제 사용자 정보가 담긴 'response' 객체 추출
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getResponse() {
        try {
            return (Map<String, Object>) attributes.get("response");
        } catch (ClassCastException e) {
            log.error("네이버 응답 데이터 형식 오류. attributes: {}", attributes, e);
            return null;
        }
    }
}