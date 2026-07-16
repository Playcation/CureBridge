package com.example.contentservice.config;

import com.example.commonmodule.security.AbstractSecurityConfig;
import com.example.commonmodule.utils.Role;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@EnableWebSecurity
public class SecurityConfig extends AbstractSecurityConfig {

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @Bean
  public JwtDecoder jwtDecoder() {
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);

    SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA384");

    return NimbusJwtDecoder.withSecretKey(secretKeySpec)
        .macAlgorithm(MacAlgorithm.HS384)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // 게이트웨이(8080)와 프론트엔드(3000) 모두 허용
    config.setAllowedOrigins(java.util.Arrays.asList("http://localhost:3000", "http://localhost:8080", "https://www.curebridge.site"));
    // PATCH를 포함한 모든 메서드 허용
    config.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(java.util.Arrays.asList("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  // TODO: 모듈별 권한 세부 설정
  @Override
  protected void configureAuthorization(HttpSecurity http) throws Exception {
    String[] whiteList = {"/api/example", "/v3/api-docs/**", "/support/**", "/swagger-ui/**",
        "/swagger-ui.html"};
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
        .requestMatchers(whiteList).permitAll()
        .requestMatchers("/api/org-admin/content/orgs/*/notice/**")
        .hasRole(Role.ORG_ADMIN.getAuthority())
        .requestMatchers("/ocr/upload/**").hasRole(Role.ORG_MANAGER.getAuthority())
        .anyRequest().permitAll()
    );
    //super.configureJwtResourceServer(http);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http)
      throws Exception {
    super.commonHttpConfig(http);
    configureAuthorization(http);
    return http.build();
  }
}
