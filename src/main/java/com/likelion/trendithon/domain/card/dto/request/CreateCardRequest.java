package com.likelion.trendithon.domain.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateCardRequest {

  @Schema(description = "카드 아이콘", example = "모바일 키보드 이모지")
  private String emoji;

  @Schema(description = "카드 제목", example = "멋쟁이사자 되기")
  private String title;

  @Schema(description = "카드 내용", example = "나는 오늘 멋쟁이 사자가 되었다.")
  private String content;

  @Schema(description = "카드 표지", example = "#000000")
  private String cover;
}
