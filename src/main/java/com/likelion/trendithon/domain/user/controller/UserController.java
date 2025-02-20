package com.likelion.trendithon.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.trendithon.domain.user.dto.request.DuplicateCheckRequest;
import com.likelion.trendithon.domain.user.dto.request.LoginRequest;
import com.likelion.trendithon.domain.user.dto.request.SignUpRequest;
import com.likelion.trendithon.domain.user.dto.response.DuplicateCheckResponse;
import com.likelion.trendithon.domain.user.service.UserService;
import com.likelion.trendithon.domain.user.util.NicknameGenerator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User 관리 API")
public class UserController {

  private final UserService userService;

  @Operation(summary = "[ 토큰 X | 회원가입 ]", description = "사용자 회원가입")
  @PostMapping("/register")
  public ResponseEntity<?> register(
      @Parameter(description = "회원가입 정보") @RequestBody SignUpRequest signUpRequest) {
    String nickname = NicknameGenerator.generateNickname();

    return userService.register(signUpRequest, nickname);
  }

  @Operation(summary = "[ 토큰 X | 랜덤 닉네임 생성 ]", description = "랜덤 닉네임 생성")
  @PostMapping("/generate-nickname")
  public String generateNickname() {
    return NicknameGenerator.generateNickname();
  }

  @Operation(summary = "[ 토큰 X | 로그인 ]", description = "사용자 로그인")
  @PostMapping("/login")
  public ResponseEntity<?> login(
      @Parameter(description = "로그인 정보") @RequestBody LoginRequest loginRequest) {
    return userService.login(loginRequest);
  }

  @Operation(summary = "[ 토큰 X | 아이디 중복 검사 ]", description = "아이디 중복 검사")
  @PostMapping("/check-duplicate")
  public ResponseEntity<DuplicateCheckResponse> checkLoginIdDuplicate(
      @Parameter(description = "중복 검사할 아이디") @RequestBody DuplicateCheckRequest request) {
    return userService.checkLoginIdDuplicate(request);
  }
}
