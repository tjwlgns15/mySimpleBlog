package com.jihun.mysimpleblog.board.repository;

import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.board.entity.Post;
import com.jihun.mysimpleblog.board.entity.PostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);
}
