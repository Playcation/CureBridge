package com.example.gateway.security;

import static org.springframework.security.config.Customizer.*;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {

	private final ReactiveJwtDecoder reactiveJwtDecoder;

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.authorizeExchange(exchanges -> exchanges
				.pathMatchers("/api/user/auth/**", "/api/auth/**").permitAll()
				.anyExchange().authenticated()
			)
		.oauth2ResourceServer(oauth2 -> oauth2
			.jwt(withDefaults())
		);
		return http.build();
	}
}
