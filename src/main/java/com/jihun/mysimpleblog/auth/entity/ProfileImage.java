package com.jihun.mysimpleblog.auth.entity;

import com.jihun.mysimpleblog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_images")
@Getter
@NoArgsConstructor
public class ProfileImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // user_id 컬럼 추가
    private User user;

    @Builder
    public ProfileImage(String url, User user) {
        this.url = url;
        this.user = user;
    }
}