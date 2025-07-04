package com.jihun.mysimpleblog.security.oauth2.userinfo.impl;

import com.jihun.mysimpleblog.domain.SocialProvider;
import com.jihun.mysimpleblog.security.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        if (attributes.get("id") == null) {
            log.error("카카오 사용자 ID를 찾을 수 없습니다. attributes: {}", attributes);
            throw new IllegalArgumentException("카카오 사용자 ID를 찾을 수 없습니다.");
        }
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public String getUsername() {
        Map<String, Object> kakaoAccount = getKakaoAccount();
        if (kakaoAccount == null || kakaoAccount.get("email") == null) {
            log.error("카카오 사용자 이메일을 찾을 수 없습니다. attributes: {}", attributes);
            throw new IllegalArgumentException("카카오 사용자 이메일을 찾을 수 없습니다.");
        }
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> properties = getProperties();
        if (properties == null || properties.get("nickname") == null) {
            log.warn("카카오 사용자 닉네임을 찾을 수 없습니다. attributes: {}", attributes);
            return "카카오 사용자"; // 기본값
        }
        return properties.get("nickname").toString();
    }

    @Override
    public String getProfileImageUrl() {
        Map<String, Object> properties = getProperties();
        if (properties != null && properties.get("profile_image") != null) {
            return properties.get("profile_image").toString();
        }
        return null;
    }

    /**
     * 카카오 API 응답에서 'kakao_account' 객체 추출
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoAccount() {
        try {
            return (Map<String, Object>) attributes.get("kakao_account");
        } catch (ClassCastException e) {
            log.error("카카오 계정 정보 형식 오류. attributes: {}", attributes, e);
            return null;
        }
    }

    /**
     * 카카오 API 응답에서 'properties' 객체 추출
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getProperties() {
        try {
            return (Map<String, Object>) attributes.get("properties");
        } catch (ClassCastException e) {
            log.error("카카오 프로퍼티 정보 형식 오류. attributes: {}", attributes, e);
            return null;
        }
    }
}