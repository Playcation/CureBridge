package com.example.memberservice.security;

import static org.springframework.security.config.Customizer.*;

import javax.crypto.SecretKey;

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

	@Bean
	public JwtDecoder jwtDecoder() {
		// HS256 검증용 NimbusJwtDecoder
		return NimbusJwtDecoder
			.withSecretKey(jwtSecretKey)
			.macAlgorithm(MacAlgorithm.HS384)
			.build();
	}
/*

	// 비밀번호 암호화 클래스 빈 등록
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private final String[] WHITE_LIST = new String[] {
		"/api/user/auth/signup", "/api/user/auth/login", "/error", "api/auth/refresh"
	};

	@Bean
	public JwtDecoder jwtDecoder() {
		// HS256 검증용 NimbusJwtDecoder
		return NimbusJwtDecoder
			.withSecretKey(jwtSecretKey)
			.macAlgorithm(MacAlgorithm.HS384)
			.build();
	}

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
		JwtIssuer jwtIssuer, JwtParser jwtParser) throws Exception {

		CustomLoginFilter loginFilter = new CustomLoginFilter(authManager, jwtIssuer);
		CustomLogoutFilter logoutFilter = new CustomLogoutFilter(jwtIssuer, jwtParser);

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

		http.addFilterBefore(new JwtAuthFilter(jwtIssuer, jwtParser), CustomLoginFilter.class);
		http.addFilterBefore(logoutFilter, LogoutFilter.class);
		http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

		http.oauth2ResourceServer(oauth2 -> oauth2
			.jwt(withDefaults())
		);

		return http.build();
	}
*/

	private final JwtIssuer jwtIssuer;
	private final JwtParser jwtParser;

	@Override
	protected void configureAuthorization(HttpSecurity http) throws Exception {
		String[] whiteList = { "/api/user/auth/signup", "/api/user/auth/login" };
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
