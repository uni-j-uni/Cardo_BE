package com.likelion.trendithon.global.auth;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.NoSuchElementException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.likelion.trendithon.domain.user.entity.User;
import com.likelion.trendithon.domain.user.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // Authorization 헤더에서 JWT 토큰을 가져옴
      final String authorizationHeader = request.getHeader("Authorization");

      // 인증 헤더가 없는 경우 다음 필터로
      if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      String jwt = authorizationHeader.substring(7);
      String loginId = jwtUtil.extractLoginId(jwt);

      // 토큰은 유효하지만 SecurityContext에 인증 정보가 없는 경우
      if (loginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        User user =
            userRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (jwtUtil.validateToken(jwt)) {
          UserDetails userDetails =
              new org.springframework.security.core.userdetails.User(
                  user.getLoginId(),
                  user.getPassword(),
                  Collections.singletonList(
                      new SimpleGrantedAuthority("ROLE_" + user.getUserRole())));

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);

          log.debug("Security Context에 '{}' 인증 정보를 저장했습니다", loginId);
        }
      }

      filterChain.doFilter(request, response);

    } catch (ExpiredJwtException e) {
      log.warn("만료된 JWT 토큰입니다. URI: {}", request.getRequestURI());
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "만료된 토큰입니다.");
    } catch (SignatureException | MalformedJwtException e) {
      log.warn("유효하지 않은 JWT 토큰입니다. URI: {}", request.getRequestURI());
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
    } catch (UsernameNotFoundException e) {
      log.warn("존재하지 않는 사용자입니다. URI: {}", request.getRequestURI());
      sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    } catch (ServletException e) {
      Throwable cause = e.getCause();
      handleException(cause, request, response);
    } catch (Exception e) {
      handleException(e, request, response);
    }
  }

  private void handleException(
      Throwable e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    int statusCode;
    String message = e.getMessage();

    if (e instanceof IllegalArgumentException || e instanceof NoSuchElementException) {
      statusCode = HttpServletResponse.SC_NOT_FOUND; // 404
      if (message == null) message = "요청한 리소스를 찾을 수 없습니다.";
    } else if (e instanceof IllegalStateException) {
      statusCode = HttpServletResponse.SC_BAD_REQUEST; // 400
      if (message == null) message = "잘못된 요청입니다.";
    } else if (e instanceof AccessDeniedException) {
      statusCode = HttpServletResponse.SC_FORBIDDEN; // 403
      if (message == null) message = "접근 권한이 없습니다.";
    } else if (e instanceof AuthenticationException) {
      statusCode = HttpServletResponse.SC_UNAUTHORIZED; // 401
      if (message == null) message = "인증에 실패했습니다.";
    } else if (e instanceof ExpiredJwtException) {
      statusCode = HttpServletResponse.SC_UNAUTHORIZED; // 401
      message = "만료된 토큰입니다.";
    } else if (e instanceof SignatureException || e instanceof MalformedJwtException) {
      statusCode = HttpServletResponse.SC_UNAUTHORIZED; // 401
      message = "유효하지 않은 토큰입니다.";
    } else {
      statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
      message = "서버 내부 오류가 발생했습니다.";
    }

    log.error(
        "필터 처리 중 예외 발생 - URI: {}, Method: {}, Error: {}, ErrorType: {}",
        request.getRequestURI(),
        request.getMethod(),
        e.getMessage(),
        e.getClass().getSimpleName());

    sendErrorResponse(response, statusCode, message);
  }

  private void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
      throws IOException {
    response.setStatus(statusCode);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(String.format("{\"isSuccess\":false,\"message\":\"%s\"}", message));
  }

  // 특정 경로는 필터 적용 제외 (예: 로그인, 회원가입)
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/auth/");
  }
}
