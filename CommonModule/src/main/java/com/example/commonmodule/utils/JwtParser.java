package com.example.commonmodule.utils;

import com.example.commonmodule.exceptions.AdminException;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TODO: JwtUtil 에서 토큰 정보 가져오는 메서드만 common 으로 옮기는 중
 * 	(비동기 처리용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtParser {

  private final SecretKey secretKey;

  // 토큰으로부터 유저 정보를 가져옴
  public Claims getUserInfoFromToken(String token) {
    log.info("Received token: {}", token); // 토큰 값 찍기
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

  // Bearer 제거한 토큰으로 유저 orgId 문자열 추출
  public String parseOrgId(String token) {
    Claims claims = getUserInfoFromToken(token);
    return claims.get("orgId", Integer.class).toString();
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

  // 토근으로 유저 orgId 검색
  public Long findOrgIdByToken(String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer", "").trim();
    return Long.parseLong(this.parseOrgId(token));
  }

  public void checkAdmin(String authorizationHeader) {
    String role = parseRole(authorizationHeader);
    if (!Role.ADMIN.toString().equals(role)) {
      throw new NoAuthorizedException(AdminException.NO_AUTHORIZED_ADMIN);
    }
  }

  public void checkOrgOrManager(String authorizationHeader) {
    String role = parseRole(authorizationHeader);
    if (!Role.ORG_MANAGER.toString().equals(role) && !Role.ORG_ADMIN.toString().equals(role)) {
      throw new NoAuthorizedException(AdminException.NO_AUTHORIZED_ADMIN);
    }
  }
}
