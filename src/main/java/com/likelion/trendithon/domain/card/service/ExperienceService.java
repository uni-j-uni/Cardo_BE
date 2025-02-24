package com.likelion.trendithon.domain.card.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.trendithon.domain.card.dto.request.CreateExperienceRequest;
import com.likelion.trendithon.domain.card.dto.request.UpdateExperienceRequest;
import com.likelion.trendithon.domain.card.dto.response.CreateExperienceResponse;
import com.likelion.trendithon.domain.card.dto.response.ExperienceResponse;
import com.likelion.trendithon.domain.card.entity.Card;
import com.likelion.trendithon.domain.card.entity.Experience;
import com.likelion.trendithon.domain.card.repository.CardRepository;
import com.likelion.trendithon.domain.card.repository.ExperienceRepository;
import com.likelion.trendithon.domain.user.entity.User;
import com.likelion.trendithon.domain.user.repository.UserRepository;
import com.likelion.trendithon.global.auth.JwtUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ExperienceService {

  private final ExperienceRepository experienceRepository;
  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  // 경험 생성
  @Transactional
  public ResponseEntity<CreateExperienceResponse> createExperience(
      Long cardId,
      CreateExperienceRequest createExperienceRequest,
      HttpServletRequest httpServletRequest) {

    try {
      String loginId =
          jwtUtil.extractLoginId(httpServletRequest.getHeader("Authorization").substring(7));
      User user =
          userRepository
              .findByLoginId(loginId)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

      Card card =
          cardRepository
              .findById(cardId)
              .orElseThrow(() -> new IllegalArgumentException("카드를 찾을 수 없습니다."));

      Experience experience =
          Experience.builder()
              .user(user)
              .card(card)
              .state(true)
              .cover(createExperienceRequest.getCover())
              .startDate(createExperienceRequest.getStartDate())
              .endDate(createExperienceRequest.getEndDate())
              .build();

      experienceRepository.save(experience);

      user.setState(true);
      userRepository.save(user);

      log.info(
          "[POST /api/cards/] 경험 생성 성공 - 생성한 사용자 ID: {}, 카드 ID: {}, 경험 ID: {}",
          user.getLoginId(),
          card.getCardId(),
          experience.getExperienceId());
      return ResponseEntity.ok(
          CreateExperienceResponse.builder()
              .success(true)
              .message("경험 생성에 성공하였습니다.")
              .cardId(card.getCardId())
              .experienceId(experience.getExperienceId())
              .build());
    } catch (Exception e) {
      log.error("[POST /api/cards/create] 경험 생성 실패 - 에러: {}", e.getMessage());
      return ResponseEntity.ok(
          CreateExperienceResponse.builder().success(false).message("경험 생성에 실패하였습니다.").build());
    }
  }

  // 사용자가 도전 중인 경험 조회
  @Transactional
  public ResponseEntity<ExperienceResponse> getEnableExperience(
      HttpServletRequest httpServletRequest) {

    try {
      String loginId =
          jwtUtil.extractLoginId(httpServletRequest.getHeader("Authorization").substring(7));
      User user =
          userRepository
              .findByLoginId(loginId)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
      Experience experience =
          experienceRepository
              .findByStateAndUserLoginId(true, user.getLoginId())
              .orElseThrow(() -> new IllegalArgumentException("사용자 경험이 존재하지 않습니다."));

      return ResponseEntity.ok(
          ExperienceResponse.builder()
              .success(true)
              .message("경험 조회에 성공하였습니다.")
              .experienceId(experience.getExperienceId())
              .build());
    } catch (IllegalArgumentException e) {
      log.error("[GET /api/cards/experience] 특정 경험 조회 실패");
      return ResponseEntity.ok(
          ExperienceResponse.builder().success(false).message(e.getMessage()).build());
    } catch (Exception e) {
      log.error("[GET /api/cards/experience] 특정 경험 조회 실패 - 에러: {}", e.getMessage());
      return ResponseEntity.ok(
          ExperienceResponse.builder().success(false).message("경험 조회 중 오류가 발생하였습니다.").build());
    }
  }

  // 사용자가 도전 중인 경험 수정
  @Transactional
  public ResponseEntity<ExperienceResponse> updateExperience(
      UpdateExperienceRequest updateExperienceRequest, HttpServletRequest httpServletRequest) {

    try {
      String loginId =
          jwtUtil.extractLoginId(httpServletRequest.getHeader("Authorization").substring(7));
      User user =
          userRepository
              .findByLoginId(loginId)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
      Experience experience =
          experienceRepository
              .findByStateAndUserLoginId(true, user.getLoginId())
              .orElseThrow(() -> new IllegalArgumentException("사용자 경험이 존재하지 않습니다."));

      experience.setEndDate(updateExperienceRequest.getEndDate());
      experienceRepository.save(experience);

      return ResponseEntity.ok(
          ExperienceResponse.builder()
              .success(true)
              .message("경험 수정에 성공하였습니다.")
              .experienceId(experience.getExperienceId())
              .build());
    } catch (IllegalArgumentException e) {
      log.error("[PUT /api/cards/experience] 특정 경험 수정 실패");
      return ResponseEntity.ok(
          ExperienceResponse.builder().success(false).message(e.getMessage()).build());
    } catch (Exception e) {
      log.error("[PUT /api/cards/experience] 특정 경험 수정 실패 - 에러: {}", e.getMessage());
      return ResponseEntity.ok(
          ExperienceResponse.builder().success(false).message("경험 수정 중 오류가 발생하였습니다.").build());
    }
  }

  // 사용자가 도전 중인 경험 포기
  @Transactional
  public ResponseEntity<ExperienceResponse> quitExperience(HttpServletRequest httpServletRequest) {

    try {
      String loginId =
          jwtUtil.extractLoginId(httpServletRequest.getHeader("Authorization").substring(7));
      User user =
          userRepository
              .findByLoginId(loginId)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
      Experience experience =
          experienceRepository
              .findByStateAndUserLoginId(true, user.getLoginId())
              .orElseThrow(() -> new IllegalArgumentException("사용자 경험이 존재하지 않습니다."));

      experience.setState(false);
      experienceRepository.save(experience);

      return ResponseEntity.ok(
          ExperienceResponse.builder()
              .success(true)
              .message("경험 수정에 성공하였습니다.")
              .experienceId(experience.getExperienceId())
              .build());
    } catch (IllegalArgumentException e) {
      log.error("[PUT /api/cards/experience] 특정 경험 수정 실패");
      return ResponseEntity.ok(
          ExperienceResponse.builder().success(false).message(e.getMessage()).build());
    } catch (Exception e) {
      log.error("[PUT /api/cards/experience] 특정 경험 수정 실패 - 에러: {}", e.getMessage());
      return ResponseEntity.ok(
          ExperienceResponse.builder().success(false).message("경험 수정 중 오류가 발생하였습니다.").build());
    }
  }
}
