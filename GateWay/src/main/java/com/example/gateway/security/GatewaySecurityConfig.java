package com.example.gateway.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.example.commonmodule.utils.Role;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.authorizeExchange(exchanges -> exchanges
				.pathMatchers("/api/user/auth/**", "/api/auth/**").permitAll() // 로그인, 회원가입은 통과
				.pathMatchers("/api/admin/**").access((authMono, ctx) -> // 이후 권한 크기순으로 체크
					authMono.map(auth -> hasRoleOrHigher(auth, Role.ADMIN))
				)
				.pathMatchers("/api/org-admin/**").access((authMono, ctx) ->
					authMono.map(auth -> hasRoleOrHigher(auth, Role.ORG_ADMIN))
				)
				.pathMatchers("/api/org-manager/**").access((authMono, ctx) ->
					authMono.map(auth -> hasRoleOrHigher(auth, Role.ORG_MANAGER))
				)
				.pathMatchers("/api/user/**").access((authMono, ctx) ->
					authMono.map(auth -> hasRoleOrHigher(auth, Role.USER))
				)
				.anyExchange().authenticated()
			)
		.oauth2ResourceServer(oauth2 -> oauth2
			.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
		);
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
		config.setAllowedHeaders(List.of("Content-Type","Authorization","X-Requested-With"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	/**
	 *
	 * @return Jwt 에서 권한 부분을 ROLE_role 형식으로 변환하여 AbstractAuthenticationToken 포함한 Mono 로 반환
	 */
	@Bean
	public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
		return jwt -> {
			Claims claims = (Claims)jwt.getClaims();
			List<String> roles = (List<String>) claims.get("roles");
			List<GrantedAuthority> authorities = roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());

			return Mono.just(new JwtAuthenticationToken(jwt, authorities));
		};
	}

	/**
	 * 권한 크기를 비교
	 *
	 * @param authentication 비교할 권한이 담긴 auth 객체
	 * @param requiredRole 필요 권한
	 * @return 권한이 더 높으면 allow
	 */
	private AuthorizationDecision hasRoleOrHigher(Authentication authentication, Role requiredRole) {
		return new AuthorizationDecision(
			authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.map(Role::fromAuthority)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.anyMatch(role -> role.isHigherThanOrEqual(requiredRole))
		);
	}

}
