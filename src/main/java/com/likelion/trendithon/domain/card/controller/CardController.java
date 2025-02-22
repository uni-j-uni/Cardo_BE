package com.likelion.trendithon.domain.card.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.trendithon.domain.card.dto.request.CardRequest;
import com.likelion.trendithon.domain.card.service.CardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cards")
@Tag(name = "Card", description = "Card 관리 API")
public class CardController {

  private CardService cardService;

  @Operation(summary = "[ 토큰 O | 카드 등록 ]", description = "새로운 카드 등록")
  @PostMapping("/create")
  public ResponseEntity<?> createCard(@RequestBody CardRequest card) {
    return cardService.createCard(card);
  }

  @Operation(summary = "[ 토큰 O | 카드 조회 ]", description = "ID를 통해 특정 카드 조회")
  @GetMapping("/{id}")
  public ResponseEntity<?> getCardById(@PathVariable Long id) {
    return cardService.getCardById(id);
  }

  @Operation(summary = "[ 토큰 O | 카드 목록 조회 ]", description = "사용자 ID를 통해 특정 카드 조회")
  @GetMapping("/all/{userId}")
  public ResponseEntity<?> getAllCards(@PathVariable String userId) {
    return cardService.getAllCards(userId);
  }

  @Operation(summary = "[ 토큰 O | 카드 삭제 ]", description = "ID를 통해 특정 카드 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCard(@PathVariable Long id) {
    return cardService.deleteCard(id);
  }

  @Operation(summary = "[ 토큰 O | 카드 수정 ]", description = "ID를 통해 특정 카드 수정")
  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateCard(@PathVariable Long id, @RequestBody CardRequest updatedCard) {
    return cardService.updateCard(id, updatedCard);
  }
}
