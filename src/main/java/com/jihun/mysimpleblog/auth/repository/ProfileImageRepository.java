package com.jihun.mysimpleblog.auth.repository;

import com.jihun.mysimpleblog.auth.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}