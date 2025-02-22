package com.likelion.trendithon.domain.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TagDto {

  @Schema(description = "태그 제목", example = "팁")
  private String tagTitle;

  @Schema(description = "태그 내용", example = "열심히 공부하기")
  private String tagContent;
}
