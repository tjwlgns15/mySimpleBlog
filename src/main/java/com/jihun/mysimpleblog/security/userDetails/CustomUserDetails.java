package com.jihun.mysimpleblog.security.userDetails;

import com.jihun.mysimpleblog.domain.SocialProvider;
import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.domain.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    // 일반 로그인용 생성자
    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getRoleName())
        );
        this.attributes = Map.of(); // 빈 Map
    }

    // OAuth2 로그인용 생성자
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getRoleName())
        );
        this.attributes = attributes;
    }

    // OAuth2User 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // OAuth2 로그인 시에는 provider ID를 반환
        if (user.isOAuth2User()) {
            return user.getSocialId();
        }
        // 일반 로그인 시에는 username 반환
        return user.getUsername();
    }

    // UserDetails 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    // 편의 메서드들
    public Long getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getUsername();
    }

    public UserRole getRole() {
        return user.getRole();
    }

    public boolean isOAuth2User() {
        return user.isOAuth2User();
    }

    public SocialProvider getSocialProvider() {
        return user.getSocialProvider();
    }
}
