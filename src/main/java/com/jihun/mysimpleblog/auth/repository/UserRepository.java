package com.jihun.mysimpleblog.auth.repository;

import com.jihun.mysimpleblog.auth.entity.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profileImage WHERE u.id = :id")
    Optional<User> findByIdWithProfileImage(Long id);

}
