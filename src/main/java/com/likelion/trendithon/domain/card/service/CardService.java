package com.likelion.trendithon.domain.card.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.trendithon.domain.card.dto.request.CreateCardRequest;
import com.likelion.trendithon.domain.card.dto.response.CardListResponse;
import com.likelion.trendithon.domain.card.dto.response.CardResponse;
import com.likelion.trendithon.domain.card.dto.response.CreateCardResponse;
import com.likelion.trendithon.domain.card.dto.response.DeleteCardResponse;
import com.likelion.trendithon.domain.card.dto.response.RandomCardResponse;
import com.likelion.trendithon.domain.card.entity.Card;
import com.likelion.trendithon.domain.card.repository.CardRepository;
import com.likelion.trendithon.domain.user.entity.User;
import com.likelion.trendithon.domain.user.repository.UserRepository;
import com.likelion.trendithon.global.auth.JwtUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CardService {

  private CardRepository cardRepository;
  private UserRepository userRepository;
  private JwtUtil jwtUtil;

  // 카드 생성
  @Transactional
  public ResponseEntity<CreateCardResponse> createCard(
      CreateCardRequest createCardRequest, HttpServletRequest httpServletRequest) {

    try {
      String loginId =
          jwtUtil.extractLoginId(httpServletRequest.getHeader("Authorization").substring(7));
      User user =
          userRepository
              .findByLoginId(loginId)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

      Card card =
          Card.builder()
              .loginId(user.getLoginId())
              .emoji(createCardRequest.getEmoji())
              .title(createCardRequest.getTitle())
              .content(createCardRequest.getContent())
              .cover(createCardRequest.getCover())
              .build();

      cardRepository.save(card);

      user.setState(false);
      userRepository.save(user);

      log.info(
          "[POST /api/cards/create] 카드 생성 성공 - 생성한 사용자 ID: {}, 카드 ID: {}",
          user.getLoginId(),
          card.getCardId());
      return ResponseEntity.ok(
          CreateCardResponse.builder()
              .success(true)
              .message("카드 생성에 성공하였습니다.")
              .cardId(card.getCardId())
              .build());
    } catch (Exception e) {
      log.error("[POST /api/cards/create] 카드 생성 실패 - 에러: {}", e.getMessage());
      return ResponseEntity.ok(
          CreateCardResponse.builder().success(false).message("카드 생성에 실패하였습니다.").build());
    }
  }

  // 랜덤 카드 세 장 조회
  @Transactional
  public ResponseEntity<RandomCardResponse> getRandomCards() {

    try {
      List<Card> cardList = cardRepository.findAll();
      Set<Long> cardIds = new HashSet<>();

      if (cardList.size() < 3) {
        return ResponseEntity.ok(
            RandomCardResponse.builder().success(false).message("전체 카드 개수가 3개 미만입니다.").build());
      } else {
        long seed = System.currentTimeMillis(); // 현재 시간
        Random random = new Random(seed); // seed 추가

        while (cardIds.size() < 3) {
          cardIds.add(random.nextLong(cardList.size()));
        }
      }

      log.info("[GET /api/cards/random] 랜덤 카드 3장 조회 성공 - 조회한 카드 ID 리스트: {}", cardIds);
      return ResponseEntity.ok(
          RandomCardResponse.builder()
              .success(true)
              .message("랜덤 카드 3장 조회에 성공하였습니다.")
              .cardIds(cardIds.stream().toList())
              .build());
    } catch (Exception e) {
      log.error("[GET /api/cards/random] 랜덤 카드 3장 조회 실패 - 에러: {}", e.getMessage());
      return ResponseEntity.ok(
          RandomCardResponse.builder()
              .success(false)
              .message("랜덤 카드 3장 조회 중 오류가 발생하였습니다.")
              .build());
    }
  }

  // 카드 한 장 조회
  @Transactional
  public ResponseEntity<CardResponse> getCardById(Long id) {

    try {
      Card card =
          cardRepository
              .findById(id)
              .orElseThrow(() -> new IllegalArgumentException("카드를 찾을 수 없습니다."));
      log.info("[GET /api/cards/{}] 특정 카드 조회 성공 - 조회한 카드 ID: {}", id, id);
      return ResponseEntity.ok(
          CardResponse.builder().success(true).message("카드 조회에 성공하였습니다.").card(card).build());
    } catch (IllegalArgumentException e) {
      log.error("[GET /api/cards/{}] 특정 카드 조회 실패", id);
      return ResponseEntity.ok(
          CardResponse.builder().success(false).message(e.getMessage()).build());
    } catch (Exception e) {
      log.error("[GET /api/cards/{}] 특정 카드 조회 실패 - 에러: {}", id, e.getMessage());
      return ResponseEntity.ok(
          CardResponse.builder().success(false).message("카드 조회 중 오류가 발생하였습니다.").build());
    }
  }

  // 모든 카드 조회
  @Transactional
  public ResponseEntity<CardListResponse> getAllCards() {
    try {
      List<Card> cardList = cardRepository.findAll();

      log.info("[GET /api/cards] 카드 전체 조회 성공 - 조회한 카드 개수: {}", cardList.size());
      return ResponseEntity.ok(
          CardListResponse.builder()
              .success(true)
              .message("카드 전체 조회에 성공하였습니다.")
              .cardList(cardList)
              .build());

    } catch (Exception e) {
      log.error("[GET /api/cards] 카드 전체 조회 실패 - 에러: {}", e.getMessage());
      return ResponseEntity.ok(
          CardListResponse.builder().success(false).message("카드 전체 조회 중 오류가 발생하였습니다.").build());
    }
  }

  // 카드 삭제
  @Transactional
  public ResponseEntity<DeleteCardResponse> deleteCard(Long id) {
    try {
      Card card =
          cardRepository
              .findById(id)
              .orElseThrow(() -> new IllegalArgumentException("카드를 찾을 수 없습니다."));
      log.info("[DELETE /api/cards/{}] 특정 카드 삭제 성공 - 삭제한 카드 ID: {}", id, id);
      return ResponseEntity.ok(
          DeleteCardResponse.builder().success(true).message("카드 삭제에 성공하였습니다.").cardId(id).build());
    } catch (Exception e) {
      return ResponseEntity.ok(
          DeleteCardResponse.builder().success(false).message("카드 삭제 중 오류가 발생했습니다.").build());
    }
  }
}
