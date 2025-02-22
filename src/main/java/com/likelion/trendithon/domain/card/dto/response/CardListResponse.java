package com.likelion.trendithon.domain.card.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CardListResponse {

  @Schema(description = "카드 생성 결과", example = "true")
  private boolean success;

  @Schema(description = "응답 메세지", example = "성공적으로 카드가 생성되었습니다.")
  private String message;

  @Schema(description = "보유한 카드 리스트")
  private List<CardListSummaryDto> cardList;
}
