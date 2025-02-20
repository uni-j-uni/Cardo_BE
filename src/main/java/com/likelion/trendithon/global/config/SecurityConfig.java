package com.likelion.trendithon.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 보호 기능 비활성화 (REST API에서는 필요없음)
        .csrf(CsrfConfigurer::disable)
        // HTTP Basic 인증 비활성화 (JWT를 사용하므로 필요없음)
        .httpBasic(HttpBasicConfigurer::disable)
        // 세션 관리 설정
        .sessionManagement(
            sessionManagement ->
                // 세션을 생성하지 않음 (JWT 사용으로 인한 Stateless 설정)
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // CORS 설정
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // HTTP 요청에 대한 권한 설정
        .authorizeHttpRequests(
            request ->
                request
                    // 인증 없이 접근 가능한 경로 설정
                    .requestMatchers(
                        "/",
                        "/api/**", // 테스트용 모든 API 열어놓음. 나중에 삭제
                        "/swagger-ui/**", // Swagger UI
                        "/v3/api-docs/**" // API 문서
                        )
                    .permitAll()
                    // 로그인이 필요한 경로 설정
                    .requestMatchers(HttpMethod.POST, "/api/**")
                    .hasRole("USER")
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest()
                    .authenticated());

    return http.build();
  }

  // CORS 설정을 위한 Bean 등록
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // 모든 출처 허용
    configuration.addAllowedOrigin("http://localhost:5173"); // 개발 서버
    configuration.addAllowedOrigin("https://localhost:5173"); // 배포 서버
    // 모든 HTTP 메서드 허용
    configuration.addAllowedMethod("*");
    // 모든 헤더 허용
    configuration.addAllowedHeader("*");
    // 자격 증명 허용 (쿠키 등)
    configuration.setAllowCredentials(true);

    // 모든 경로에 대해 위의 CORS 설정 적용
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  /** 비밀번호 인코더 Bean 등록 */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
