package com.jihun.mysimpleblog.repository;

import com.jihun.mysimpleblog.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.author " +     // ManyToOne은 페치 조인
            "WHERE c.post.id = :postId " +
            "AND c.parent IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<Comment> findByPostIdWithAuthor(@Param("postId") Long postId, Pageable pageable);

}
