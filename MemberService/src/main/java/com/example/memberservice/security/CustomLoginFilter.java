package com.example.memberservice.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.memberservice.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public CustomLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/api/user/auth/login");
	}

	// JSON Body 에서 자격증명 파싱
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
		HttpServletResponse response)
		throws AuthenticationException {
		try {
			LoginRequestDto creds = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword());
			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new AuthenticationServiceException("Invalid login request format", e);
		}
	}

	// 인증 성공 시 JWT 발급
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain,
		Authentication authResult) throws IOException {
		String token = jwtUtil.generateToken(authResult);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), Map.of("accessToken", token));
	}

	// 인증 실패 시 에러 응답
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		log.info("[LoginFilter] faild: {}", failed.getMessage());
		objectMapper.writeValue(response.getWriter(), Map.of("error", failed.getMessage()));
	}
}
