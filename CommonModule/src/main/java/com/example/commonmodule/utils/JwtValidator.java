package com.example.commonmodule.utils;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

/**
 * TODO: JWTUtil 에서 검증 로직만 가져옴
 */
@Component
@RequiredArgsConstructor
public class JwtValidator {

	private final SecretKey secretKey;

	// 토큰이 유효하지 않으면 예외처리
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);   // 서명과 만료(exp)까지 자동 검증
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			// - ExpiredJwtException (만료)
			// - MalformedJwtException (형식 오류)
			// - SecurityException / SignatureException (서명 불일치)
			return false;
		}
	}

	// 토큰 만료 검사
	public void isExpired(String token) {
		Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.getExpiration().before(new Date());
	}

	// 토큰 종류 반환
	public String getCategory(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("category", String.class);
	}
}
