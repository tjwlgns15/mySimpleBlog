package com.jihun.mysimpleblog.board.repository;

import com.jihun.mysimpleblog.board.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId);

    @Query("SELECT DISTINCT c FROM Comment c " +
            "LEFT JOIN FETCH c.children " +
            "LEFT JOIN FETCH c.author " +
            "WHERE c.post.id = :postId " +
            "AND c.parent IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<Comment> findByPostIdWithChildrenAndAuthor(@Param("postId") Long postId, Pageable pageable);

}
