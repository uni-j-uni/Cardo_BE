package com.likelion.trendithon.domain.card.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.trendithon.domain.card.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
  List<Card> findByUserId(String id);
}
