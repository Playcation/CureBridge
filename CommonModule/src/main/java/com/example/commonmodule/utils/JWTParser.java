package com.example.commonmodule.utils;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * TODO: JwtUtil 에서 토큰 정보 가져오는 메서드만 common 으로 옮기는 중
 * 	(비동기 처리용)
 */
public class JWTParser {
	// 토큰으로부터 유저 정보를 가져옴
	public Claims getUserInfoFromToken(String token, SecretKey secretKey) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
