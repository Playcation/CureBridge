package com.example.memberservice.security;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.example.commonmodule.security.AbstractSecurityConfig;
import com.example.commonmodule.utils.JwtParser;
import com.example.memberservice.filter.CustomLoginFilter;
import com.example.memberservice.filter.CustomLogoutFilter;
import com.example.memberservice.filter.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig extends AbstractSecurityConfig {

	private final SecretKey jwtSecretKey;
	private final JwtIssuer jwtIssuer;
	private final JwtParser jwtParser;

	@Bean
	public JwtDecoder jwtDecoder() {
		// HS256 검증용 NimbusJwtDecoder
		return NimbusJwtDecoder
			.withSecretKey(jwtSecretKey)
			.macAlgorithm(MacAlgorithm.HS384)
			.build();
	}

	@Override
	protected void configureAuthorization(HttpSecurity http) throws Exception {
		// TODO: 세부 권한, 화이트리스트 등록
		String[] whiteList = {"/user/auth/signup", "/user/auth/login"};
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers(whiteList).permitAll()
			.requestMatchers("/api/admin/**").hasRole("ADMIN")
			.anyRequest().authenticated()
		);
		super.configureJwtResourceServer(http);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager am) throws Exception {
		super.commonHttpConfig(http);
		configureAuthorization(http);

		CustomLoginFilter loginFilter = new CustomLoginFilter(am, jwtIssuer);
		CustomLogoutFilter logoutFilter = new CustomLogoutFilter(jwtIssuer, jwtParser);

		// 모듈 전용 필터 추가
		http.addFilterBefore(new JwtAuthFilter(jwtIssuer, jwtParser), CustomLoginFilter.class);
		http.addFilterBefore(logoutFilter, LogoutFilter.class);
		http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
