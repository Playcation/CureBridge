package com.example.memberservice.security;

import com.example.memberservice.entity.OrgManager;
import com.example.memberservice.entity.Organization;
import com.example.memberservice.entity.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.example.commonmodule.config.TokenSettings;
import java.text.ParseException;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtIssuer {

  private final SecretKey secretKey;
  private final UserDetailsServiceImpl userDetailsService;
  private final ManagerDetailsServiceImpl managerDetailsService;
  private final OrganizationDetailsServiceImpl organizationDetailsService;
  private final RedisTemplate<String, String> redisTemplate;

  // request 에 담긴 토큰 가져옴 + "Bearer " 제거
  public String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader(TokenSettings.ACCESS_TOKEN_CATEGORY);
    log.info(bearer);
    if (bearer != null && bearer.startsWith("Bearer ")) {
      try {
        SignedJWT signedJWT = SignedJWT.parse(bearer.substring(7));
        JWSAlgorithm alg = signedJWT.getHeader().getAlgorithm();

        System.out.println("JWS Algorithm: " + alg.getName());
      } catch (ParseException e) {
        log.info(e.getMessage());
      }
      return bearer.substring(7);
    }
    return null;
  }

  // 인증 객체 생성
  public Authentication createAuthentication(String username) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  // 토큰 생성
  public String[] generateUserToken(Authentication auth) {
    UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
    User user = principal.getUser();
    return generateUserToken(user.getEmail(), user.getId(), auth.getAuthorities());
  }

  public String[] generateManagerToken(Authentication auth) {
    ManagerDetailsImpl principal = (ManagerDetailsImpl) auth.getPrincipal();
    OrgManager manager = principal.getManager();
    return generateUserToken(manager.getEmail(), manager.getId(), auth.getAuthorities());
  }

  public String[] generateOrganizationToken(Authentication auth) {
    OrganizationDetailsImpl principal = (OrganizationDetailsImpl) auth.getPrincipal();
    Organization organization = principal.getOrganization();
    return generateUserToken(organization.getEmail(), organization.getId(), auth.getAuthorities());
  }

  public String[] generateUserToken(String email, Long id,
      Collection<? extends GrantedAuthority> authCollect) {

    List<String> authList = authCollect.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    String accessToken = generateAccessToken(email, id, authList);
    String refreshToken = generateRefreshToken(id);

    // 레디스에 refresh 토큰 저장
    String redisKey = TokenSettings.REFRESH_TOKEN_CATEGORY + id.toString();
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(redisKey, refreshToken, Duration.ofMillis(TokenSettings.REFRESH_TOKEN_EXPIRATION));

    return new String[]{accessToken, refreshToken};
  }

  // Access Token 생성
  private String generateAccessToken(String email, Long id, List<String> authList) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + TokenSettings.ACCESS_TOKEN_EXPIRATION);

    return Jwts.builder()
        .issuer(TokenSettings.TOKEN_ISSUER)
        .subject(email)
        .claim("userId", id)
        .claim("roles", authList)
        .claim("category", TokenSettings.ACCESS_TOKEN_CATEGORY)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(secretKey)
        .compact();
  }

  // Refresh Token 생성
  private String generateRefreshToken(Long id) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + TokenSettings.REFRESH_TOKEN_EXPIRATION);

    return Jwts.builder()
        .issuer(TokenSettings.TOKEN_ISSUER)
        .claim("userId", id)
        .claim("category", TokenSettings.REFRESH_TOKEN_CATEGORY)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(secretKey)
        .compact();
  }

  public Cookie createCookie(String key, String value, int maxAge) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(maxAge);
    cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }

  // 레디스에 저장된 정보로 유저 검증
  public boolean checkUserRefreshTokenFromRedis(String id, String refresh) {
    String redisKey = TokenSettings.REFRESH_TOKEN_CATEGORY + id;
    String storedToken = redisTemplate.opsForValue().get(redisKey);
    return refresh.equals(storedToken);
  }

  // 레디스에서 리프레시 토큰 삭제
  public void deleteRefreshTokenInRedis(String id) {
    String redisKey = TokenSettings.REFRESH_TOKEN_CATEGORY + id;
    redisTemplate.delete(redisKey);
  }

  public Authentication createManagerAuthentication(String username) {
    UserDetails userDetails = managerDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  public Authentication createOrganizationAuthentication(String username) {
    UserDetails userDetails = organizationDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}
