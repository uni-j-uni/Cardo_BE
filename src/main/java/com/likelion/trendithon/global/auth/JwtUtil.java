package com.likelion.trendithon.global.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {
  @Value("${secret-key}")
  private String secretKey;

  // access token 만료 기간 1시간
  // private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
  private static final long ACCESS_TOKEN_EXPIRE_TIME = TimeUnit.DAYS.toMillis(7);
  // refresh token 만료 기간 30일
  private static final long REFRESH_TOKEN_EXPIRE_TIME = TimeUnit.DAYS.toMillis(30);

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  // Access Token 발급 부분
  public String createAccessToken(String loginId) {
    Date now = new Date();
    Date expireTime = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
    Key key = getSigningKey();

    return Jwts.builder()
        .setSubject(loginId)
        .setIssuedAt(new Date())
        .setExpiration(expireTime)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // Refresh Token 발급 부분
  public String createRefreshToken(String userId) {
    Date now = new Date();
    Date expireTime = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
    Key key = getSigningKey();
    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(new Date())
        .setExpiration(expireTime)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰 검증 부분
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다.");
    }
    return false;
  }

  // JWT 토큰 Claims에서 모든 정보 추출
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // 토큰에서 loginId 추출
  public String extractLoginId(String token) {
    return getAllClaimsFromToken(token).getSubject();
  }

  // 토큰에서 만료 시간 추출
  public Date extractExpiration(String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  // 토큰이 만료되었는지 확인
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}
