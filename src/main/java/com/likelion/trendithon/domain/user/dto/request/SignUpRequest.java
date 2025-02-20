package com.likelion.trendithon.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
  @Schema(description = "아이디", example = "cardoteam0226")
  private String loginId;

  @Schema(description = "비밀번호")
  private String password;

  @Schema(description = "닉네임", example = "용감한 사자")
  private String nickname;
}
