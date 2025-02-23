package com.likelion.trendithon.domain.card.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeleteCardResponse {

  @Schema(description = "카드 삭제 결과", example = "true")
  private boolean success;

  @Schema(description = "응답 메세지", example = "카드 삭제에 성공하였습니다.")
  private String message;

  @Schema(description = "삭제된 카드 ID", example = "1")
  private Long cardId;
}
