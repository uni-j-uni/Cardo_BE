package com.likelion.trendithon.domain.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.trendithon.domain.card.entity.Card;
import com.likelion.trendithon.domain.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

  List<Tag> findByCard(Card card);
}
