package com.likelion.trendithon.domain.user.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.trendithon.domain.user.dto.request.DuplicateCheckRequest;
import com.likelion.trendithon.domain.user.dto.request.LoginRequest;
import com.likelion.trendithon.domain.user.dto.request.SignUpRequest;
import com.likelion.trendithon.domain.user.dto.response.DuplicateCheckResponse;
import com.likelion.trendithon.domain.user.dto.response.LoginResponse;
import com.likelion.trendithon.domain.user.dto.response.SignUpResponse;
import com.likelion.trendithon.domain.user.entity.User;
import com.likelion.trendithon.domain.user.repository.UserRepository;
import com.likelion.trendithon.global.auth.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Transactional
  public ResponseEntity<SignUpResponse> register(SignUpRequest request, String nickname) {
    try {
      // 중복 회원 검사
      if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
        log.warn("[POST /api/users/register] 회원가입 실패 - 이미 존재하는 ID: {}", request.getLoginId());
        return ResponseEntity.ok(
            SignUpResponse.builder().success(false).message("이미 존재하는 아이디입니다.").build());
      }

      // 비밀번호 암호화
      String encodedPassword = passwordEncoder.encode(request.getPassword());

      User user =
          User.builder()
              .loginId(request.getLoginId())
              .password(encodedPassword)
              .nickname(nickname)
              .userRole("USER")
              .build();
      userRepository.save(user);

      log.info(
          "[POST /api/users/register] 회원가입 성공 - ID: {}, 닉네임: {}",
          user.getLoginId(),
          user.getNickname());
      return ResponseEntity.ok(
          SignUpResponse.builder().success(true).message("회원가입이 완료되었습니다.").build());
    } catch (Exception e) {
      log.error("[POST /api/users/register] 회원가입 실패 - ID: {}", request.getLoginId());
      return ResponseEntity.ok(
          SignUpResponse.builder().success(false).message("회원가입 처리 중 오류가 발생했습니다.").build());
    }
  }

  // 로그인
  @Transactional
  public ResponseEntity<LoginResponse> login(LoginRequest request) {
    try {
      // 1. 로그인 아이디로 사용자 찾기
      User user =
          userRepository
              .findByLoginId(request.getLoginId())
              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

      // 2. 비밀번호 확인
      if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return ResponseEntity.ok(
            LoginResponse.builder().success(false).message("잘못된 비밀번호입니다.").build());
      }

      // 3. 토큰 생성
      String accessToken = jwtUtil.createAccessToken(user.getLoginId());
      String refreshToken = jwtUtil.createRefreshToken(user.getLoginId());

      // 4. refreshToken DB에 저장
      user.setRefreshToken(refreshToken);
      userRepository.save(user);

      // 5. 로그인 성공 응답 생성
      log.info(
          "[POST /api/users/login] 로그인 성공 - 아이디: {} 닉네임: {}",
          user.getLoginId(),
          user.getNickname());
      return ResponseEntity.ok(
          LoginResponse.builder()
              .accessToken(accessToken)
              .success(true)
              .message("로그인에 성공하였습니다.")
              .build());

    } catch (IllegalArgumentException e) {
      // 6. 존재하지 않는 아이디인 경우
      log.warn("[POST /api/users/login] 로그인 실패 - 잘못된 아이디: {}", request.getLoginId());
      return ResponseEntity.ok(
          LoginResponse.builder().success(false).message(e.getMessage()).build());
    } catch (Exception e) {
      // 7. 기타 예외 처리
      log.error(
          "[POST /api/users/login] 로그인 오류 발생 - 아이디: {}, 에러: {}",
          request.getLoginId(),
          e.getMessage());
      return ResponseEntity.ok(
          LoginResponse.builder().success(false).message("로그인 처리 중 서버 오류 발생." + e).build());
    }
  }

  public ResponseEntity<DuplicateCheckResponse> checkLoginIdDuplicate(
      DuplicateCheckRequest request) {
    // 로그인 아이디(이메일) 존재 여부 확인
    boolean isDuplicate = userRepository.findByLoginId(request.getLoginId()).isPresent();

    // 응답 생성
    DuplicateCheckResponse response =
        DuplicateCheckResponse.builder()
            .result(isDuplicate)
            .message(isDuplicate ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.")
            .build();

    return ResponseEntity.ok(response);
  }
}
