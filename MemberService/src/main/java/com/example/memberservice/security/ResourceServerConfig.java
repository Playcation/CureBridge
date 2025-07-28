package com.example.memberservice.security;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/*@Configuration
@EnableWebSecurity
@Order(2)*/
public class ResourceServerConfig {
	/*@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// CSRF, CORS 등 필요한 보안 헤더 설정
			.csrf(AbstractHttpConfigurer::disable)

			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// 1. 인가 규칙 정의
			.authorizeHttpRequests(authorize -> authorize
				// 허용 경로
				.requestMatchers("/auth/login", "/auth/signup").permitAll()
				.anyRequest().authenticated()
			)

			// 2. OAuth2 리소스 서버(JWT) 활성화
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(withDefaults())
			);

		return http.build();
	}*/
}
