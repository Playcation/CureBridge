package com.example.commonmodule.utils;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

/**
 * TODO: JwtUtil 에서 토큰 정보 가져오는 메서드만 common 으로 옮기는 중
 * 	(비동기 처리용)
 */
@Component
@RequiredArgsConstructor
public class JwtParser {

	private final SecretKey secretKey;

	// 토큰으로부터 유저 정보를 가져옴
	public Claims getUserInfoFromToken(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	// Bearer 제거한 토큰으로 유저 id 문자열 추출
	public String parseUserId(String token) {
		Claims claims = getUserInfoFromToken(token);
		return claims.get("userId", Integer.class).toString();
	}

	// Bearer 제거한 토큰으로 유저 role(권한) 문자열 추출
	public String parseRole(String token) {
		Claims claims = getUserInfoFromToken(token);
		return claims.get("roles", String.class);
	}

	// 토근으로 유저 id 검색
	public Long findUserByToken(String authorizationHeader) {
		String token = authorizationHeader.replace("Bearer", "").trim();
		/*
		// 토큰 검증은 GateWay 에서 마무리
		if (!validateToken(token)) {
			throw new IllegalArgumentException("Invalid JWT token");
		}*/
		return Long.parseLong(this.parseUserId(token));
	}
}
