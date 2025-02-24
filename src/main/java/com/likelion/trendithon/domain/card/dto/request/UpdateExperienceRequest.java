package com.likelion.trendithon.domain.card.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateExperienceRequest {

  @Schema(description = "종료 날짜", example = "2025.01.01")
  private LocalDate endDate;
}
