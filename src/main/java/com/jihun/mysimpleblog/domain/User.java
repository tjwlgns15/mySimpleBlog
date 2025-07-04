package com.jihun.mysimpleblog.domain;

import com.jihun.mysimpleblog.infra.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider socialProvider;

    @Column
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    @Column(nullable = false)
    private boolean enabled;

    private User(String username, String password, String name, UserRole role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role != null ? role : UserRole.GUEST;
        this.socialProvider = SocialProvider.LOCAL;
        this.enabled = true;
    }

    private User(String username, String name, UserRole role, SocialProvider socialProvider, String socialId) {
        this.username = username;
        this.name = name;
        this.role = role != null ? role : UserRole.GUEST;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.enabled = true;
    }

    public static User createUser(String username, String password, String name, UserRole role) {
        return new User(username, password, name, role);
    }

    public static User createOAuth2User(String username, String name, UserRole role, SocialProvider socialProvider, String socialId) {
        return new User(username, name, role, socialProvider, socialId);
    }

    public void updateProfile(String name, String introduction) {
        this.name = name;
        this.introduction = introduction;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void updateProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    public boolean isOAuth2User() {
        return this.socialProvider != SocialProvider.LOCAL;
    }

    public boolean isLocalUser() {
        return this.socialProvider == SocialProvider.LOCAL;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    private String generateUsernameFromEmail(String email) {
        return email.split("@")[0] + "_" + System.currentTimeMillis();
    }
}
