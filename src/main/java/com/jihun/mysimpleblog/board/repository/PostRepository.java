package com.jihun.mysimpleblog.board.repository;

import com.jihun.mysimpleblog.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.author " +
            "JOIN FETCH p.category " +
            "WHERE (:title IS NULL OR p.title LIKE %:title%) " +
            "AND (:authorName IS NULL OR p.author.name LIKE %:authorName%)")
    Page<Post> findAllBySearchCondition(
            @Param("title") String title,
            @Param("authorName") String authorName,
            Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.author " +
            "LEFT JOIN FETCH p.category " +
            "WHERE p.id = :id")
    Optional<Post> findByIdWithAuthorAndCategory(@Param("id") Long id);

    boolean existsByCategoryId(Long id);

}
