package com.example.memberservice.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class UserTypeAuthenticationToken extends UsernamePasswordAuthenticationToken {

  // userType을 저장할 필드
  private final String userType;

  // 인증 요청 시 사용할 생성자 (아직 인증되지 않음)
  public UserTypeAuthenticationToken(Object principal, Object credentials, String userType) {
    super(principal, credentials);
    this.userType = userType;
    setAuthenticated(false); // 인증 요청 토큰이므로 'false'
  }

}
