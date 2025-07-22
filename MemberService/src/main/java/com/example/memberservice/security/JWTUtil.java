package com.example.memberservice.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {

	private final SecretKey secretKey;

	public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	// Bearer 제거한 토큰으로 유저 id 문자열 추출
	public String parseUserId(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("userId", String.class);
	}

	// 토근으로 유저 id 검색
	public Long findUserByToken(String authorizationHeader) {
		String token = authorizationHeader.replace("Bearer", "").trim();
		return Long.parseLong(this.parseUserId(token));
	}
}
