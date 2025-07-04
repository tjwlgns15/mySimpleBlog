package com.jihun.mysimpleblog.security.oauth2.userinfo.impl;

import com.jihun.mysimpleblog.domain.SocialProvider;
import com.jihun.mysimpleblog.security.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        if (attributes.get("sub") == null) {
            log.error("구글 사용자 ID를 찾을 수 없습니다. attributes: {}", attributes);
            throw new IllegalArgumentException("구글 사용자 ID를 찾을 수 없습니다.");
        }
        return attributes.get("sub").toString();
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.GOOGLE;
    }

    @Override
    public String getUsername() {
        if (attributes.get("email") == null) {
            log.error("구글 사용자 이메일을 찾을 수 없습니다. attributes: {}", attributes);
            throw new IllegalArgumentException("구글 사용자 이메일을 찾을 수 없습니다.");
        }
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        if (attributes.get("name") == null) {
            log.warn("구글 사용자 이름을 찾을 수 없습니다. attributes: {}", attributes);
            return "Google 사용자"; // 기본값
        }
        return attributes.get("name").toString();
    }

    @Override
    public String getProfileImageUrl() {
        return (String) attributes.get("picture");
    }
}