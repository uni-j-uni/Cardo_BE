package com.likelion.trendithon.domain.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.trendithon.domain.card.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {}
