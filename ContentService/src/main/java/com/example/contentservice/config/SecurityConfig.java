package com.example.contentservice.config;

import com.example.commonmodule.security.AbstractSecurityConfig;
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
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

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

  // TODO: 모듈별 권한 세부 설정
  @Override
  protected void configureAuthorization(HttpSecurity http) throws Exception {
    String[] whiteList = {"/api/example", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"};
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers(whiteList).permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers(RegexRequestMatcher.regexMatcher("/orgs/.*/notices/.*"))
        .hasRole("ORG_ADMIN").anyRequest().permitAll()
    );
    super.configureJwtResourceServer(http);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http)
      throws Exception {
    super.commonHttpConfig(http);
    configureAuthorization(http);
    return http.build();
  }
}
