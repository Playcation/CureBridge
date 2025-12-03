package com.example.memberservice.service;

import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.TokenErrorCode;
import com.example.commonmodule.utils.JwtParser;
import com.example.commonmodule.utils.JwtValidator;
import com.example.memberservice.entity.User;
import com.example.memberservice.security.JwtIssuer;
import com.example.commonmodule.config.TokenSettings;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenService {

  private final JwtIssuer jwtIssuer;
  private final JwtValidator jwtValidator;
  private final JwtParser jwtParser;
  private final UserService userService;
  private final RedisTemplate<String, String> redisTemplate;

  public String[] createNewToken(HttpServletRequest request) {

    log.info("Start createNewToken");
    String refreshToken = "";

    // 1. 리프레시 토큰 얻기
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
          String token = cookie.getValue();
          if (token != null && token.startsWith("refresh=")) {
            token = token.substring(8);
          }
          log.info("token: {}", token);
          refreshToken = token;
        }
      }
    } else {
      log.warn("No cookies found in the request");
    }
    if (refreshToken == null || refreshToken.isEmpty()) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // 2. 리프레시 토큰 만료 검사
    try {
      jwtValidator.isExpired(refreshToken);
    } catch (ExpiredJwtException e) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }
    if (!TokenSettings.REFRESH_TOKEN_CATEGORY.equals(jwtValidator.getCategory(refreshToken))) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // 3. 리프레시 토큰 유효성 검사
    String userId = jwtParser.parseUserId(refreshToken);
    if (!jwtIssuer.checkUserRefreshTokenFromRedis(userId, refreshToken)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // 4. 새 토큰 발급
    User user = userService.findUserById(Long.parseLong(userId));
    return jwtIssuer.generateUserToken(user.getEmail(), user.getId(), user.getOrganizationId(),
        user.getRole().toGrantedAuthorities());
  }

  // 리프레시 토큰 담은 쿠키 반환
  public Cookie getRefreshCookie(String refresh) {
    return jwtIssuer.createCookie(
        TokenSettings.REFRESH_TOKEN_CATEGORY,
        refresh,
        TokenSettings.COOKIE_EXPIRATION
    );
  }
}


