package com.example.contentservice.config;

import com.example.commonmodule.security.AbstractSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends AbstractSecurityConfig {

  // TODO: 모듈별 권한 세부 설정
  @Override
  protected void configureAuthorization(HttpSecurity http) throws Exception {
    String[] whiteList = {"/api/example", "/support/**"};
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers(whiteList).permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/orgs/**/notices/**").hasRole("ORG_ADMIN")
        .anyRequest().permitAll()
    );
    super.configureJwtResourceServer(http);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager am)
      throws Exception {
    super.commonHttpConfig(http);
    configureAuthorization(http);
    return http.build();
  }
}
