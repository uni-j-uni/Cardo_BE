package com.likelion.trendithon.domain.card.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CardListSummaryDto {

  @Schema(description = "카드 Id", example = "1234")
  private Long cardId;

  @Schema(description = "카드 제목", example = "멋쟁이사자 되기")
  private String title;
}
