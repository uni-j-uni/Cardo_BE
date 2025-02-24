package com.likelion.trendithon.domain.card.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateExperienceResponse {

  @Schema(description = "경험 수정 결과", example = "true")
  private boolean success;

  @Schema(description = "응답 메세지", example = "경험 수정에 성공하였습니다.")
  private String message;

  @Schema(description = "수정된 경험 ID", example = "1")
  private Long experienceId;
}
