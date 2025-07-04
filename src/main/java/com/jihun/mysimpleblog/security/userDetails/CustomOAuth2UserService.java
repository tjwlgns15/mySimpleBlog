package com.jihun.mysimpleblog.security.userDetails;

import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.repository.UserRepository;
import com.jihun.mysimpleblog.security.oauth2.factory.OAuth2UserInfoFactory;
import com.jihun.mysimpleblog.security.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 실패");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (oAuth2UserInfo.getUsername() == null || oAuth2UserInfo.getUsername().isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 제공자로부터 사용자명을 가져올 수 없습니다.");
        }

        User user = userRepository.findByUsernameAndSocialProvider(
                        oAuth2UserInfo.getUsername(),
                        oAuth2UserInfo.getProvider()
                )
                .map(existingUser -> updateExistingUser(existingUser, oAuth2UserInfo))
                .orElseGet(() -> registerNewUser(oAuth2UserInfo));

        // OAuth2User로 반환 (CustomUserDetails는 OAuth2User도 구현하므로 가능)
        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {

        User user = User.createOAuth2User(
                oAuth2UserInfo.getUsername(),
                oAuth2UserInfo.getName(),
                null,
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getProviderId()
        );

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {

        existingUser.updateProfile(
                oAuth2UserInfo.getName(),
                oAuth2UserInfo.getProfileImageUrl()
        );

        return userRepository.save(existingUser);
    }
}
