package com.example.memberservice.filter;

import com.example.commonmodule.utils.JwtParser;
import com.example.commonmodule.utils.Role;
import com.example.memberservice.security.JwtIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtIssuer jwtIssuer;
  private final JwtParser jwtParser;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // request 에 담긴 토큰을 가져온다.
    String token = jwtIssuer.resolveToken(request);

    // 토큰이 null 이면 다음 필터로 넘어간다.
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

		/*
		// TODO: 유효성 검증 로직 GateWay 로 이동
		// 토큰이 유효하지 않으면 예외처리
		if (!jwtValidators.validateToken(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}*/

    // 유효한 토큰이라면, 토큰으로부터 사용자 정보를 가져온다.
    Claims info = jwtParser.getUserInfoFromToken(token);
    String role = ((DefaultClaims) info).get("roles").toString().replace("[", "").replace("]", "");
    setAuthentication(info.getSubject(), role);   // 사용자 정보로 인증 객체 만들기

    filterChain.doFilter(request, response);
  }

  private void setAuthentication(String username, String role) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = null;
    if (Role.USER.toString().equals(role) || Role.ADMIN.toString().equals(role)) {
      authentication = jwtIssuer.createAuthentication(username); // 인증 객체 만들기
    } else if (Role.ORG_MANAGER.toString().equals(role)) {
      authentication = jwtIssuer.createManagerAuthentication(username); // 인증 객체 만들기
    } else if (Role.ORG_ADMIN.toString().equals(role)) {
      authentication = jwtIssuer.createOrganizationAuthentication(username); // 인증 객체 만들기
    }
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }
  
}
