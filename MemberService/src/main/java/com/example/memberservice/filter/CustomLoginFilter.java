package com.example.memberservice.filter;

import com.example.commonmodule.config.TokenSettings;
import com.example.memberservice.dto.LoginRequestDto;
import com.example.memberservice.security.JwtIssuer;
import com.example.memberservice.security.ManagerDetailsImpl;
import com.example.memberservice.security.OrganizationDetailsImpl;
import com.example.memberservice.security.UserDetailsImpl;
import com.example.memberservice.security.UserTypeAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtIssuer jwtIssuer;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public CustomLoginFilter(AuthenticationManager authenticationManager, JwtIssuer jwtIssuer) {
    this.authenticationManager = authenticationManager;
    this.jwtIssuer = jwtIssuer;
    setFilterProcessesUrl("/user/auth/login");
  }

  // JSON Body 에서 자격증명 파싱
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response)
      throws AuthenticationException {
    try {
      LoginRequestDto creds = objectMapper.readValue(request.getInputStream(),
          LoginRequestDto.class);
      UserTypeAuthenticationToken authToken =
          new UserTypeAuthenticationToken(
              creds.getEmail(),
              creds.getPassword(),
              creds.getRole().toString()
          );
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

    Object principal = authResult.getPrincipal();

    String[] tokens;
    Long userId = null;
    String userRole = null;

    if (principal instanceof UserDetailsImpl) {
      UserDetailsImpl userDetails = (UserDetailsImpl) principal;
      tokens = jwtIssuer.generateUserToken(authResult);
      userId = userDetails.getUser().getId(); // ✅ User ID 추출
      userRole = "USER"; // ✅ 역할 설정

    } else if (principal instanceof ManagerDetailsImpl) {
      ManagerDetailsImpl managerDetails = (ManagerDetailsImpl) principal;
      tokens = jwtIssuer.generateManagerToken(authResult);
      userId = managerDetails.getManager().getId(); // ✅ Manager ID 추출
      userRole = "ORG_MANAGER"; // ✅ 역할 설정

    } else if (principal instanceof OrganizationDetailsImpl) {
      OrganizationDetailsImpl orgDetails = (OrganizationDetailsImpl) principal;
      tokens = jwtIssuer.generateOrganizationToken(authResult);
      userId = orgDetails.getOrganization().getId(); // ✅ Organization ID 추출
      userRole = "ORG_ADMIN"; // ✅ 역할 설정

    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writeValue(response.getWriter(),
          Map.of("error", "Unknown authentication principal"));
      return;
    }

    String accessToken = tokens[0];
    String refreshToken = tokens[1];

    // refresh token 저장한 쿠키 생성
    Cookie cookie = jwtIssuer.createCookie(
        TokenSettings.REFRESH_TOKEN_CATEGORY,
        refreshToken,
        TokenSettings.COOKIE_EXPIRATION);
    response.addCookie(cookie);

    // access token 응답 설정
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), Map.of(
        "accessToken", accessToken,
        "userId", userId,
        "userRole", userRole
    ));
  }

  // 인증 실패 시 에러 응답
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    log.info("[LoginFilter] failed: {}", failed.getMessage());
    objectMapper.writeValue(response.getWriter(), Map.of("error", failed.getMessage()));
  }
}
