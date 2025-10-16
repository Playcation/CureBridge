package com.example.gateway.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
				.pathMatchers("/api/anonymous/**").permitAll() // 로그인, 회원가입은 통과
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
			Object rawRoles = jwt.getClaims().get("roles");

			List<String> roles;
			if (rawRoles instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> rawList = (List<Object>) rawRoles;
				roles = rawList.stream()
					.filter(Objects::nonNull)
					.map(Object::toString)
					.collect(Collectors.toList());
			} else if (rawRoles instanceof String) {
				String s = (String) rawRoles;
				roles = Arrays.stream(s.split(","))
					.map(String::trim)
					.filter(r -> !r.isEmpty())
					.collect(Collectors.toList());
			} else {
				roles = Collections.emptyList();
			}

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
