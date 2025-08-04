package com.example.memberservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.example.memberservice.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWTUtil {

	private final SecretKey secretKey;
	private final UserDetailsServiceImpl userDetailsService;

	public JWTUtil(@Value("${spring.jwt.secret}") String secret, UserDetailsServiceImpl userDetailsService) {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
		this.userDetailsService = userDetailsService;
	}

	// request 에 담긴 토큰 가져옴 + "Bearer " 제거
	public String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader(TokenSettings.ACCESS_TOKEN_CATEGORY);
		if (bearer != null && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}

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
		return claims.get("userId", String.class);
	}

	// Bearer 제거한 토큰으로 유저 role(권한) 문자열 추출
	public String parseRole(String token) {
		Claims claims = getUserInfoFromToken(token);
		return claims.get("roles", String.class);
	}

	// 토근으로 유저 id 검색
	public Long findUserByToken(String authorizationHeader) {
		String token = authorizationHeader.replace("Bearer", "").trim();
		if (!validateToken(token)) {
			// TODO: 커스텀 예외 처리
			throw new IllegalArgumentException("Invalid JWT token");
		}
		return Long.parseLong(this.parseUserId(token));
	}

	// 인증 객체 생성
	public Authentication createAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	// 토큰 생성
	public String generateToken(Authentication auth) {
		UserDetailsImpl principal = (UserDetailsImpl)auth.getPrincipal();
		User user = principal.getUser();
		return generateToken(user.getEmail(), user.getId(), auth.getAuthorities());
	}

	public String generateToken(String email, Long id, Collection<? extends GrantedAuthority> authCollect) {

		List<String> authList = authCollect.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toList());

		Date now = new Date();
		Date expiry = new Date(now.getTime() + TokenSettings.ACCESS_TOKEN_EXPIRATION);

		return Jwts.builder()
			.issuer(TokenSettings.TOKEN_ISSUER)
			.subject(email)
			.claim("userId", id)
			.claim("roles", authList)
			.issuedAt(now)
			.expiration(expiry)
			.signWith(secretKey)
			.compact();
	}

	// 리프레시 토큰 새로 생성
	public String generateRefreshToken(String id) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + TokenSettings.REFRESH_TOKEN_EXPIRATION);

		return Jwts.builder()
			.issuer(TokenSettings.TOKEN_ISSUER)
			.claim("userId", id)
			.issuedAt(now)
			.expiration(expiry)
			.signWith(secretKey)
			.compact();
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
