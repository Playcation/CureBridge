package com.example.commonmodule.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Primary
@Configuration
@RequiredArgsConstructor
public abstract class AbstractSecurityConfig {

//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}

//  @Bean
//  public AuthenticationManager authenticationManager(
//      AuthenticationConfiguration authenticationConfiguration) throws Exception {
//    return authenticationConfiguration.getAuthenticationManager();
//  }

  protected void commonHttpConfig(HttpSecurity http) throws Exception {
    http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
  }

  protected void configureJwtResourceServer(HttpSecurity http) throws Exception {
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
  }

  protected AbstractAuthenticationToken convertJwtToAuth(Jwt jwt, List<String> roles) {
    List<GrantedAuthority> authorities = roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toList());
    return new JwtAuthenticationToken(jwt, authorities);
  }

  /**
   * 모듈별로 화이트리스트, 세부 권한을 설정할 메서드
   *
   * @param http
   * @throws Exception
   */
  protected abstract void configureAuthorization(HttpSecurity http) throws Exception;
}
