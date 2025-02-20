package com.likelion.trendithon.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DuplicateCheckRequest {
  @Schema(description = "아이디", example = "cardoteam0226")
  private String loginId;
}
