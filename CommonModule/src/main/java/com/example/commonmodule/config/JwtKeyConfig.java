package com.example.commonmodule.config;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JwtKeyConfig {

	@Value("${spring.jwt.secret}")
	private String secret;

	@Bean
	public SecretKey jwtSecretKey() {
		// System.out.println(">>> Loaded spring.jwt.secret = " + secret);

		byte[] keyBytes = Decoders.BASE64.decode(secret);
		SecretKey key = Keys.hmacShaKeyFor(keyBytes);

		// System.out.println(">>> Generated SecretKey = " + key);
		return key;
	}
}
