package com.jihun.mysimpleblog.auth.oauth2;


import com.jihun.mysimpleblog.auth.oauth2.userinfo.GoogleUserInfo;
import com.jihun.mysimpleblog.auth.oauth2.userinfo.KakaoUserInfo;
import com.jihun.mysimpleblog.auth.oauth2.userinfo.NaverUserInfo;
import com.jihun.mysimpleblog.auth.oauth2.userinfo.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class OAuth2UserInfoFactory {

    public OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        log.debug("Extracting user info for provider: {}", registrationId);

        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        };
    }
}
