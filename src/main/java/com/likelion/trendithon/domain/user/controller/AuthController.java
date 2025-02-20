package com.likelion.trendithon.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.trendithon.domain.user.repository.UserRepository;
import com.likelion.trendithon.global.auth.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User")
public class AuthController {
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Operation(
      summary = "[ 토큰 O | Refresh Token 재발급 ]",
      description = "전송된 Refresh Token이 만료되었을 경우 재발급")
  @PostMapping("/refresh-token")
  public ResponseEntity<?> refresh(@RequestHeader("Authorization") String refreshToken) {
    try {
      // Bearer 검증
      if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("Refresh Token이 필요합니다."));
      }

      String token = refreshToken.substring(7);
      // Refresh Token에서 loginId 추출
      String loginId = jwtUtil.extractLoginId(token);

      if (loginId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("유효하지 않은 Refresh Token입니다."));
      }

      // refresh Token 만료 여부 확인
      if (jwtUtil.isTokenExpired(token)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("Refresh Token이 만료되었습니다. 다시 로그인해주세요."));
      }

      // User 조회
      userRepository
          .findByLoginId(loginId)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      // 새로운 Access Token 발급
      String newAccessToken = jwtUtil.createAccessToken(loginId);
      return ResponseEntity.ok(new TokenResponse(newAccessToken));

    }
    // refresh Token 파싱 실패 (만료된 토큰)
    catch (ExpiredJwtException e) {
      log.error("만료된 Refresh Token입니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse("Refresh Token이 만료되었습니다. 다시 로그인해주세요."));

    } catch (Exception e) {
      log.error("Token 갱신 중 오류 발생: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("토큰 갱신 중 서버 오류가 발생했습니다. 잠시 후 다시시도해주세요."));
    }
  }
}

@Getter
@AllArgsConstructor
class TokenResponse {
  private String accessToken;
}

@Getter
@AllArgsConstructor
class ErrorResponse {
  private String message;
}
