package com.example.commonmodule.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
  public String parseRole(String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer", "").trim();
    Claims claims = getUserInfoFromToken(token);
    return claims.get("roles").toString().replace("[", "").replace("]", "");
  }

  // 토근으로 유저 id 검색
  public Long findUserByToken(String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer", "").trim();
    return Long.parseLong(this.parseUserId(token));
  }
}
