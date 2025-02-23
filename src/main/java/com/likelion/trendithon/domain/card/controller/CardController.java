package com.likelion.trendithon.domain.card.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.trendithon.domain.card.dto.request.CreateCardRequest;
import com.likelion.trendithon.domain.card.service.CardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
  public ResponseEntity<?> createCard(
      @Parameter(description = "카드에 포함되는 내용") @RequestBody CreateCardRequest createCardRequest,
      HttpServletRequest httpServletRequest) {
    return cardService.createCard(createCardRequest, httpServletRequest);
  }

  @Operation(summary = "[ 토큰 X | 카드 단일 조회 ]", description = "카드 ID를 통해 특정 카드 조회")
  @GetMapping("/{id}")
  public ResponseEntity<?> getCardById(@Parameter(description = "카드 ID") @PathVariable Long id) {
    return cardService.getCardById(id);
  }

  @Operation(summary = "[ 토큰 X | 랜덤 카드 3장 조회 ]", description = "경험 등록 후 랜덤 카드 3장의 ID 조회")
  @GetMapping("/random")
  public ResponseEntity<?> getRandomCards() {
    return cardService.getRandomCards();
  }

  @Operation(summary = "[ 토큰 X | 전체 카드 조회 | 테스트용 ]", description = "전체 카드 조회")
  @GetMapping()
  public ResponseEntity<?> getAllCard() {
    return cardService.getAllCards();
  }

  @Operation(summary = "[ 토큰 O | 카드 삭제 ]", description = "ID를 통해 특정 카드 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCard(@PathVariable Long id) {
    return cardService.deleteCard(id);
  }
}
