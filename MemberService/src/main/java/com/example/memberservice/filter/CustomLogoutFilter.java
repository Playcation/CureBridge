package com.example.memberservice.filter;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.example.memberservice.security.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutFilter extends GenericFilterBean {

	private final JWTUtil jwtUtil;

	public CustomLogoutFilter(JWTUtil jwtUtil) {
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
		if(!requestUri.matches("^\\/logout$")){
			filterChain.doFilter(request, response);
			return;
		}
		String method=request.getMethod();
		if(!method.equals("POST")){
			filterChain.doFilter(request,response);
			return;
		}

		// TODO: 프론트에서 access Token 값 삭제해야함...
	}
}
