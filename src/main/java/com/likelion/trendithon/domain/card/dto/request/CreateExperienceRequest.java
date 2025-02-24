package com.likelion.trendithon.domain.card.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateExperienceRequest {

  @Schema(description = "카드 표지", example = "#000000")
  private String cover;

  @Schema(description = "시작 날짜", example = "2025.01.01")
  private LocalDate startDate;

  @Schema(description = "종료 날짜", example = "2025.01.01")
  private LocalDate endDate;
}
