package com.example.memberservice.security;

import com.example.commonmodule.security.AbstractSecurityConfig;
import com.example.commonmodule.utils.JwtParser;
import com.example.memberservice.filter.CustomLoginFilter;
import com.example.memberservice.filter.CustomLogoutFilter;
import com.example.memberservice.filter.JwtAuthFilter;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig extends AbstractSecurityConfig {

  private final CustomAuthenticationProvider customAuthenticationProvider;
  private final JwtIssuer jwtIssuer;
  private final JwtParser jwtParser;

  @Bean
  public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
    // HS384 검증용 NimbusJwtDecoder
    return NimbusJwtDecoder
        .withSecretKey(jwtSecretKey)
        .macAlgorithm(MacAlgorithm.HS384)
        .build();
  }

  @Override
  protected void configureAuthorization(HttpSecurity http) throws Exception {
    // TODO: 세부 권한, 화이트리스트 등록
    String[] whiteList = {"/user/auth/signup", "/user/auth/login", "/v3/api-docs/**",
        "/swagger-ui/**", "/swagger-ui.html"};
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers(whiteList).permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        // .anyRequest().permitAll()
    );
//    super.configureJwtResourceServer(http);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      AuthenticationConfiguration authenticationConfiguration)
      throws Exception {

    // [중요] 여기서 Manager를 꺼냅니다.
    AuthenticationManager am = authenticationConfiguration.getAuthenticationManager();

    super.commonHttpConfig(http);
    configureAuthorization(http);

    CustomLoginFilter loginFilter = new CustomLoginFilter(am, jwtIssuer);
    CustomLogoutFilter logoutFilter = new CustomLogoutFilter(jwtIssuer, jwtParser);

    http.addFilterBefore(new JwtAuthFilter(jwtIssuer, jwtParser),
        UsernamePasswordAuthenticationFilter.class);
    http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(logoutFilter, LogoutFilter.class);

    return http.build();
  }
}
