package com.jihun.mysimpleblog.auth.oauth2.userinfo;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
    String getEmail();
}
