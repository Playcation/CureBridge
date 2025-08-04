package com.example.memberservice.security;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.example.memberservice.filter.CustomLoginFilter;
import com.example.memberservice.filter.CustomLogoutFilter;
import com.example.memberservice.filter.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@Order(1)
public class SecurityConfig {

	private final JWTUtil jwtUtil;

	// 비밀번호 암호화 클래스 빈 등록
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private String[] WHITE_LIST = new String[] {
		"/api/user/auth/signup", "/api/user/auth/login", "/error"
	};

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager,
		JWTUtil jwtUtil) throws Exception {

		CustomLoginFilter loginFilter = new CustomLoginFilter(authManager, jwtUtil);
		CustomLogoutFilter logoutFilter = new CustomLogoutFilter(jwtUtil);

		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

		// 화이트리스트 url + 권한 설정
		// TODO: 권한별 접근 설정
		http.authorizeHttpRequests((auth) -> auth
			.requestMatchers(WHITE_LIST).permitAll()
			.anyRequest().authenticated()
		);

		// TODO: 커스텀 필터 적용
		http.addFilterBefore(new JwtAuthFilter(jwtUtil), CustomLoginFilter.class);
		http.addFilterBefore(logoutFilter, LogoutFilter.class);
		http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

		http.oauth2ResourceServer(oauth2 -> oauth2
			.jwt(withDefaults())
		);

		return http.build();
	}
}
