package com.example.memberservice.security;

import com.example.commonmodule.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  // ✨ 각 사용자 유형별 UserDetailsService 주입
  // (Bean 이름을 다르게 하여 주입받아야 함)
  private final UserDetailsServiceImpl userDetailsService;
  private final ManagerDetailsServiceImpl managerDetailsService;
  private final OrganizationDetailsServiceImpl companyDetailsService;

  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // 1. Filter에서 생성한 커스텀 토큰을 캐스팅
    UserTypeAuthenticationToken token = (UserTypeAuthenticationToken) authentication;

    String email = (String) token.getPrincipal();
    String password = (String) token.getCredentials();
    String userType = token.getUserType();

    // 2. userType에 따라 사용할 UserDetailsService(DB 조회)를 선택
    UserDetails userDetails;
    Role role;
    try {
      role = Role.valueOf(userType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException("유효하지 않은 사용자 유형입니다.");
    }
    try {
      switch (role) {
        case USER, ADMIN:
          userDetails = userDetailsService.loadUserByUsername(email);
          break;
        case ORG_MANAGER:
          userDetails = managerDetailsService.loadUserByUsername(email);
          break;
        case ORG_ADMIN:
          userDetails = companyDetailsService.loadUserByUsername(email);
          break;
        default:
          throw new BadCredentialsException("유효하지 않은 사용자 유형입니다.");
      }
    } catch (UsernameNotFoundException e) {
      throw new BadCredentialsException("계정을 찾을 수 없습니다.", e);
    }

    // 3. 비밀번호 검증
    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
    }

    // 4. 인증 성공! (중요)
    // 인증이 완료된 토큰은 Spring Security가 인식하는 표준 토큰으로 반환합니다.
    // 이 토큰이 Filter의 successfulAuthentication 메소드로 전달됩니다.
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    // ✨ 이 Provider가 'UserTypeAuthenticationToken'을 처리하도록 명시
    return UserTypeAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
