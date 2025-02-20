package com.likelion.trendithon.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {
  @Schema(description = "회원가입 결과", example = "true")
  private boolean success;

  @Schema(description = "응답 메세지", example = "회원가입이 완료되었습니다.")
  private String message;

  @Schema(description = "닉네임", example = "용감한 사자")
  private String nickname;
}
