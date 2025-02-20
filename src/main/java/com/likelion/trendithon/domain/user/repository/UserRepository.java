package com.likelion.trendithon.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.trendithon.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByLoginId(String loginId);
}
