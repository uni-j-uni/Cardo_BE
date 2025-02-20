package com.likelion.trendithon.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DuplicateCheckResponse {
  @Schema(description = "아이디 중복 검사 결과", example = "true")
  private boolean result;

  @Schema(description = "응답 메세지", example = "사용 가능한 아이디입니다.")
  private String message;
}
