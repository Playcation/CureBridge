package com.example.memberservice.security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtKeyConfig {

	@Value("${spring.jwt.secret}")
	private String base64Secret;

	@Bean
	public SecretKey jwtSecretKey() {
		byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
