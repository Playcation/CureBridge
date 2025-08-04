package com.example.memberservice.service;

import org.springframework.stereotype.Service;

import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.TokenErrorCode;
import com.example.memberservice.entity.User;
import com.example.memberservice.security.JWTUtil;
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

	private final JWTUtil jwtUtil;
	private final UserService userService;

	public String[] createNewToken(HttpServletRequest request) {

		String refreshToken = "";

		// 1. 리프레시 토큰 얻기
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
				refreshToken = cookie.getValue();
			}
		}
		if (refreshToken.isEmpty()) {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}

		// 2. 리프레시 토큰 만료 검사
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}
		if (!jwtUtil.getCategory(refreshToken).equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}

		// 3. 리프레시 토큰 유효성 검사
		String userId = jwtUtil.parseUserId(refreshToken);
		// TODO: userId 사용해서 사용자 검증
		if () {
			throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
		}

		// 4. 새 토큰 발급
		User user = userService.findUserById(Long.parseLong(userId));
		return jwtUtil.generateToken(user.getEmail(), user.getId(),
			user.getRole().toGrantedAuthorities());
	}
}
