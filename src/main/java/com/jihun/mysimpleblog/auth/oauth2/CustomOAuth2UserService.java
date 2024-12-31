package com.jihun.mysimpleblog.auth.oauth2;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.model.Provider;
import com.jihun.mysimpleblog.auth.model.User;
import com.jihun.mysimpleblog.auth.oauth2.userinfo.OAuth2UserInfo;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.jihun.mysimpleblog.auth.model.Role.USER;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("OAuth2 로그인 처리 중 오류 발생", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private CustomUserDetails processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // OAuth2 제공자 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider authProvider = Provider.valueOf(registrationId.toUpperCase());

        // OAuth2 사용자 정보 추출
        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        String email = oAuth2UserInfo.getEmail();
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();

        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, oAuth2UserInfo, authProvider))
                .map(user -> new CustomUserDetails(user, oAuth2User.getAttributes()))
                .orElseGet(() -> createNewUser(email, name, providerId, authProvider, oAuth2User));
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, Provider authProvider) {
        // 다른 OAuth2 제공자로 가입된 경우 예외 발생
        if (existingUser.getProvider() != authProvider) {
            throw new OAuth2AuthenticationException(
                    String.format("이미 %s 계정으로 가입된 이메일입니다.", existingUser.getProvider())
            );
        }

        // 사용자 정보 업데이트
        existingUser.updateName(oAuth2UserInfo.getName());

        log.info("Existing OAuth2 user updated: {}", existingUser.getEmail());
        return userRepository.save(existingUser);
    }

    private CustomUserDetails createNewUser(String email, String name, String providerId,
                                             Provider authProvider, OAuth2User oAuth2User) {
        // 새로운 OAuth2 사용자 생성
        User newUser = User.builder()
                .email(email)
                .name(name)
                .providerId(providerId)
                .provider(authProvider)
                .role(USER)  // 기본 사용자 권한 부여
                .password(UUID.randomUUID().toString())  // 임의의 비밀번호 설정
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("New OAuth2 user created: {}", savedUser.getEmail());

        return new CustomUserDetails(savedUser, oAuth2User.getAttributes());
    }
}
