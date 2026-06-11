package com.example.memberservice.security;

import com.example.commonmodule.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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

  private final UserDetailsServiceImpl userDetailsService;
  private final ManagerDetailsServiceImpl managerDetailsService;
  private final OrganizationDetailsServiceImpl companyDetailsService;

  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    UserTypeAuthenticationToken token = (UserTypeAuthenticationToken) authentication;

    String email = (String) token.getPrincipal();
    String password = (String) token.getCredentials();
    String userType = token.getUserType();

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

    if (!userDetails.isEnabled()) {
      throw new DisabledException("탈퇴한 계정입니다.");
    }

    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
    }

    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UserTypeAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
