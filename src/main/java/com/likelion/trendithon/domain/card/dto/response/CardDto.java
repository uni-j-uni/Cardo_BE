package com.likelion.trendithon.domain.card.dto.response;

import java.util.List;

import com.likelion.trendithon.domain.tag.dto.TagDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardDto {

  @Schema(description = "카드 제목", example = "멋쟁이사자 되기")
  private String title;

  @Schema(description = "카드 내용", example = "나는 오늘 멋쟁이 사자가 되다.")
  private String content;

  @Schema(description = "이모지 Url")
  private String imgUrl;

  @Schema(description = "사용자 Id", example = "likelion")
  private String userId;

  @Schema(description = "태그 목록", example = "[{\"tagTitle\": \"팁\", \"tagContent\": \"열심히 하기\"}]")
  private List<TagDto> tagItems;
}
