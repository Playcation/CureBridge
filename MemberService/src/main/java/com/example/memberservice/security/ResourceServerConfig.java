package com.example.memberservice.security;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// CSRF, CORS 등 필요한 보안 헤더 설정
			.csrf(csrf -> csrf.disable())

			// 1. 인가 규칙 정의
			.authorizeHttpRequests(authorize -> authorize
				// 허용 경로
				.requestMatchers("/login", "/signup").permitAll()
				.anyRequest().authenticated()
			)

			// 2. OAuth2 리소스 서버(JWT) 활성화
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(withDefaults())
			);

		return http.build();
	}
}
