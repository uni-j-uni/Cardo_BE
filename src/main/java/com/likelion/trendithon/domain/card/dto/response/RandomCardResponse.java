package com.likelion.trendithon.domain.card.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RandomCardResponse {

  @Schema(description = "랜덤 카드 3장 조회 결과", example = "true")
  private boolean success;

  @Schema(description = "응답 메세지", example = "랜덤 카드 3장 조회에 성공하였습니다.")
  private String message;

  @Schema(description = "랜덤 카드 3장 ID 리스트", example = "[1, 2, 3]")
  private List<Long> cardIds;
}
