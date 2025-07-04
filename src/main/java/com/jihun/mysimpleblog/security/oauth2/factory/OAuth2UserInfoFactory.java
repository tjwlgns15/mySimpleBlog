package com.jihun.mysimpleblog.security.oauth2.factory;

import com.jihun.mysimpleblog.domain.SocialProvider;
import com.jihun.mysimpleblog.security.oauth2.userinfo.OAuth2UserInfo;
import com.jihun.mysimpleblog.security.oauth2.userinfo.impl.GoogleOAuth2UserInfo;
import com.jihun.mysimpleblog.security.oauth2.userinfo.impl.KakaoOAuth2UserInfo;
import com.jihun.mysimpleblog.security.oauth2.userinfo.impl.NaverOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return new GoogleOAuth2UserInfo(attributes);
            case "kakao":
                return new KakaoOAuth2UserInfo(attributes);
            case "naver":
                return new NaverOAuth2UserInfo(attributes);
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }
    }

    public static SocialProvider getSocialProvider(String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return SocialProvider.GOOGLE;
            case "kakao":
                return SocialProvider.KAKAO;
            case "naver":
                return SocialProvider.NAVER;
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }
    }
}
