package com.example.memberservice.security;

import com.example.memberservice.entity.Organization;
import com.example.memberservice.entity.Role;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class OrganizationDetailsImpl implements UserDetails {

  private final Organization organization;
  private final String username;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Role role = organization.getRole();
    return role.toGrantedAuthorities();
  }

  @Override
  public String getPassword() {
    return organization.getPassword();
  }

  @Override
  public String getUsername() {
    return this.username;
  }
}
