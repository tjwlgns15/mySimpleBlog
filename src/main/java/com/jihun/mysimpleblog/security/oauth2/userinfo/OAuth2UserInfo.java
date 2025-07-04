package com.jihun.mysimpleblog.security.oauth2.userinfo;

import com.jihun.mysimpleblog.domain.SocialProvider;

public interface OAuth2UserInfo {
    String getProviderId();
    SocialProvider getProvider();
    String getUsername();
    String getName();
    String getProfileImageUrl();
}
