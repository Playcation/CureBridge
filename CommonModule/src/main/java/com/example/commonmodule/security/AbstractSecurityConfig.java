package com.example.commonmodule.security;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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
    http.oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
    );
  }

  /**
   * Jwt 의 "roles" 클레임을 읽어서 GrantedAuthority 로 변환하는 Converter
   */
  private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    return jwt -> {
      // "roles": ["ADMIN", "USER"] 이런 구조라고 가정
      List<String> roles = jwt.getClaimAsStringList("roles");
      if (roles == null) {
        // 혹시 문자열로 들어갈 수도 있으면 대비
        String rolesStr = jwt.getClaimAsString("roles");
        if (rolesStr != null && !rolesStr.isBlank()) {
          roles = List.of(rolesStr.split(","));
        } else {
          roles = Collections.emptyList();
        }
      }

      // 실제 Authentication 으로 변환
      return convertJwtToAuth(jwt, roles);
    };
  }

  protected AbstractAuthenticationToken convertJwtToAuth(Jwt jwt, List<String> roles) {
    List<GrantedAuthority> authorities = roles.stream()
        //.map(String::trim)
        //.filter(r -> !r.isEmpty())
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

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        "ROLE_ADMIN > ROLE_ORG_ADMIN\n" +
        "ROLE_ORG_ADMIN > ROLE_ORG_MANAGER\n" +
        "ROLE_ORG_MANAGER > ROLE_USER");
  }
}
