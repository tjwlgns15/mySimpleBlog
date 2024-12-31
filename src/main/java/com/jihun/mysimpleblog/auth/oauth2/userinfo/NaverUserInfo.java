package com.jihun.mysimpleblog.auth.oauth2.userinfo;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return (String) ((Map<?, ?>) attributes.get("response")).get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return (String) ((Map<?, ?>) attributes.get("response")).get("name");
    }

    @Override
    public String getEmail() {
        return (String) ((Map<?, ?>) attributes.get("response")).get("email");
    }
}
