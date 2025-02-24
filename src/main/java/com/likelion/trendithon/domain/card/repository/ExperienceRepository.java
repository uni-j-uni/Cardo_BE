package com.likelion.trendithon.domain.card.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.trendithon.domain.card.entity.Experience;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
  Optional<Experience> findByStateAndUserLoginId(boolean state, String loginId);
}
