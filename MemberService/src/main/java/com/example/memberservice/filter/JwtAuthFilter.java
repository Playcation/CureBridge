package com.example.memberservice.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.memberservice.security.JWTUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		// request 에 담긴 토큰을 가져온다.
		String token = jwtUtil.resolveToken(request);

		// 토큰이 null 이면 다음 필터로 넘어간다.
		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 토큰이 유효하지 않으면 예외처리
		if (!jwtUtil.validateToken(token)) {
			// TODO: 커스텀 예외 발생시키기
			throw new IllegalArgumentException("Invalid JWT token");
		}

		// 유효한 토큰이라면, 토큰으로부터 사용자 정보를 가져온다.
		Claims info = jwtUtil.getUserInfoFromToken(token);
		setAuthentication(info.getSubject());   // 사용자 정보로 인증 객체 만들기

		filterChain.doFilter(request, response);
	}

	private void setAuthentication(String username) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = jwtUtil.createAuthentication(username); // 인증 객체 만들기
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}
}
