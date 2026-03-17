package com.example.memberservice.security;

import com.example.memberservice.entity.OrgManager;
import com.example.commonmodule.utils.Role;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class ManagerDetailsImpl implements UserDetails {

  private final OrgManager manager;
  private final String username;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Role role = manager.getRole();
    return role.toGrantedAuthorities();
  }

  @Override
  public String getPassword() {
    return manager.getPassword();
  }

  @Override
  public String getUsername() {
    return this.username;
  }
}
