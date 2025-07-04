package com.jihun.mysimpleblog.repository;

import com.jihun.mysimpleblog.domain.SocialProvider;
import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndSocialProvider(String username, SocialProvider socialProvider);
    Optional<User> findBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profileImage WHERE u.id = :userId")
    Optional<User> findByIdWithProfileImage(@Param("userId") Long userId);

    boolean existsByUsername(String username);
    boolean existsByName(String name);
    boolean existsByUsernameAndSocialProvider(String username, SocialProvider socialProvider);

    List<User> findByRole(UserRole role);
    List<User> findBySocialProvider(SocialProvider socialProvider);
    List<User> findByEnabledTrue();
    List<User> findByEnabledFalse();

    @Query("SELECT u FROM User u WHERE u.socialProvider = :provider AND u.enabled = true")
    List<User> findActiveBySocialProvider(@Param("provider") SocialProvider provider);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = true")
    List<User> findActiveByRole(@Param("role") UserRole role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.socialProvider = :provider")
    long countBySocialProvider(@Param("provider") SocialProvider provider);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") UserRole role);


}
