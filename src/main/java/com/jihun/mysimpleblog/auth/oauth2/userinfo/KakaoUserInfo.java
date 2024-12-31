package com.jihun.mysimpleblog.auth.oauth2.userinfo;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProviderId() {
//        return String.valueOf(attributes.get("id"));  // 카카오는 Long 타입
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return (String) ((Map<?, ?>) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) ((Map<?, ?>) attributes.get("kakao_account")).get("email");
    }
}
