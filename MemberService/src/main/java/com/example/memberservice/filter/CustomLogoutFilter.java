package com.example.memberservice.filter;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.example.memberservice.security.JwtUtil;
import com.example.memberservice.security.TokenSettings;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutFilter extends GenericFilterBean {

	private final JwtUtil jwtUtil;

	public CustomLogoutFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {

		this.doFilter((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse, filterChain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		IOException,
		ServletException {

		//path랑 메소드 확인
		String requestUri=request.getRequestURI();
		if(!requestUri.matches("/logout")){
			filterChain.doFilter(request, response);
			return;
		}
		String method=request.getMethod();
		if(!method.equals("POST")){
			filterChain.doFilter(request,response);
			return;
		}

		// TODO: 프론트에서 access Token 값 삭제해야함...

		// TODO: 쿠키에서 리프레시 토큰 추출
		String refreshToken = "";
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (TokenSettings.REFRESH_TOKEN_CATEGORY.equals(cookie.getName())) {
				refreshToken = cookie.getValue();
			}
		}
		if (refreshToken.isEmpty()) {
			// 예외 던지기
		}

		// TODO: 리프레시 토큰 검증
		String userId = jwtUtil.parseUserId(refreshToken);
		if (!jwtUtil.checkUserRefreshTokenFromRedis(userId, refreshToken)) {
			// 예외 던지기
		}

		// TODO: 레디스에서 리프레시 토큰 삭제
		jwtUtil.deleteRefreshTokenInRedis(userId);

		// TODO: 리프레시 토큰 쿠키 삭제
		Cookie cookie = new Cookie(TokenSettings.REFRESH_TOKEN_CATEGORY, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		response.setStatus(HttpServletResponse.SC_OK);
	}
}
