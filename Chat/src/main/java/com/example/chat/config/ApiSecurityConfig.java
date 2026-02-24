package com.example.chat.config;

import com.example.commonmodule.security.AbstractSecurityConfig;
import com.example.commonmodule.utils.Role;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity   // 🔥 메인 Security는 여기서 켜줌
// @Order(1)
public class ApiSecurityConfig extends AbstractSecurityConfig {

  @Bean
  public JwtDecoder jwtDecoder(SecretKey secretKey) {
    return NimbusJwtDecoder
        .withSecretKey(secretKey)
        .macAlgorithm(MacAlgorithm.HS384)
        .build();
  }

  @Override
  protected void configureAuthorization(HttpSecurity http) throws Exception {

    /*// 공통 설정 (세션 stateless, csrf/폼로그인/기본인증 off 등)
    commonHttpConfig(http);

    // JWT 리소스 서버 설정
    configureJwtResourceServer(http);*/

    http
        // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .securityMatcher("/chat/**")
        .authorizeHttpRequests(auth -> auth
            // 🔥 1) 프리플라이트 OPTIONS는 전부 허용 (Network Error 핵심 해결)
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // 🔥 3) WebSocket 관련 경로는 여기서도 열어둠(안전용)
            .requestMatchers("/connect/**", "/ws/**", "/stomp/**").permitAll()

            // 🔥 4) 채팅 REST API는 인증 필요
            .requestMatchers("/chat/**").hasRole(Role.USER.getAuthority())

            // 🔥 5) 나머지는 기본적으로 인증 필요
            .anyRequest().authenticated()
        );
  }

/*  // REST API용 CORS
  private CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setExposedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }*/

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http)
      throws Exception {
    super.commonHttpConfig(http);
    super.configureJwtResourceServer(http);
    configureAuthorization(http);
    return http.build();
  }
}
