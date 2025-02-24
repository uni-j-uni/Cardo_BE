package com.likelion.trendithon.domain.card.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.trendithon.domain.card.dto.request.CreateExperienceRequest;
import com.likelion.trendithon.domain.card.dto.request.UpdateExperienceRequest;
import com.likelion.trendithon.domain.card.service.ExperienceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cards")
@Tag(name = "Card", description = "Card 관리 API")
public class ExperienceController {

  private ExperienceService experienceService;

  @Operation(summary = "[ 토큰 O | 카드 경험 ]", description = "다른 사용자가 생성한 카드 경험 등록")
  @PostMapping("/experience")
  public ResponseEntity<?> createExperience(
      @Parameter(description = "경험할 카드 ID") Long cardId,
      @Parameter(description = "경험 내용") @RequestBody
          CreateExperienceRequest createExperienceRequest,
      HttpServletRequest httpServletRequest) {
    return experienceService.createExperience(cardId, createExperienceRequest, httpServletRequest);
  }

  @Operation(summary = "[ 토큰 O | 사용자 경험 조회 ]", description = "사용자가 현재 도전 중인 경험 조회")
  @GetMapping("/experience")
  public ResponseEntity<?> getEnableExperience(HttpServletRequest httpServletRequest) {
    return experienceService.getEnableExperience(httpServletRequest);
  }

  @Operation(summary = "[ 토큰 O | 사용자 경험 수정 ]", description = "사용자가 현재 도전 중인 경험 종료일 변경")
  @PutMapping("/experience")
  public ResponseEntity<?> updateExperience(
      @Parameter(description = "경험 종료일") @RequestBody
          UpdateExperienceRequest updateExperienceRequest,
      HttpServletRequest httpServletRequest) {
    return experienceService.updateExperience(updateExperienceRequest, httpServletRequest);
  }

  @Operation(summary = "[ 토큰 O | 사용자 경험 포기 ]", description = "사용자가 현재 도전 중인 경험 포기")
  @PutMapping("/experience/quit")
  public ResponseEntity<?> quitExperience(HttpServletRequest httpServletRequest) {
    return experienceService.quitExperience(httpServletRequest);
  }
}
