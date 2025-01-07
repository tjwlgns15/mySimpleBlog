package com.jihun.mysimpleblog.board.entity;

import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count")
    private int viewCount = 0;

//    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @Builder
    public Post(User author, Category category, String title, String content) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void update(Category category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public boolean addLike(User user) {
        // 이미 좋아요한 경우 false 반환
        if (this.likes.stream().anyMatch(like -> like.getUser().equals(user))) {
            return false;
        }

        PostLike like = PostLike.builder()
                .post(this)
                .user(user)
                .build();
        this.likes.add(like);
        return true;
    }

    public boolean removeLike(User user) {
        return this.likes.removeIf(like -> like.getUser().equals(user));
    }

    public int getLikeCount() {
        return this.likes.size();
    }
}
