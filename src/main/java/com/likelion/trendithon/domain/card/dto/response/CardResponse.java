package com.likelion.trendithon.domain.card.dto.response;

import com.likelion.trendithon.domain.card.entity.Card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CardResponse {

  @Schema(description = "카드 조회 결과", example = "true")
  private boolean success;

  @Schema(description = "응답 메세지", example = "카드 조회에 성공하였습니다.")
  private String message;

  @Schema(description = "조회한 카드 ID", example = "1")
  private Long cardId;
}
