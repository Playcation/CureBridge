package com.example.memberservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.TokenErrorCode;
import com.example.memberservice.entity.User;
import com.example.memberservice.security.JwtUtil;
import com.example.memberservice.security.TokenSettings;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenService {

	private final JwtUtil jwtUtil;
	private final UserService userService;
	private final RedisTemplate<String, String> redisTemplate;

	public String[] createNewToken(HttpServletRequest request) {

		log.info("Start createNewToken");
		String refreshToken = "";

		// 1. 리프레시 토큰 얻기
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
				String token = cookie.getValue();
				if (token != null && token.startsWith("refresh=")) {
					token = token.substring(8);
				}
				log.info("token: {}", token);
				refreshToken = token;
			}
		}
		if (refreshToken == null || refreshToken.isEmpty()) {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}

		// 2. 리프레시 토큰 만료 검사
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}
		if (!TokenSettings.REFRESH_TOKEN_CATEGORY.equals(jwtUtil.getCategory(refreshToken))) {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}

		// 3. 리프레시 토큰 유효성 검사
		String userId = jwtUtil.parseUserId(refreshToken);
		if (!jwtUtil.checkUserRefreshTokenFromRedis(userId, refreshToken)) {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}

		// 4. 새 토큰 발급
		User user = userService.findUserById(Long.parseLong(userId));
		return jwtUtil.generateToken(user.getEmail(), user.getId(),
			user.getRole().toGrantedAuthorities());
	}

	// 리프레시 토큰 담은 쿠키 반환
	public Cookie getRefreshCookie(String refresh) {
		return jwtUtil.createCookie(
			TokenSettings.REFRESH_TOKEN_CATEGORY,
			refresh,
			TokenSettings.COOKIE_EXPIRATION
		);
	}
}


