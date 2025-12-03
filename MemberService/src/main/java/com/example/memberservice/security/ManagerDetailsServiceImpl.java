package com.example.memberservice.security;

import com.example.memberservice.entity.OrgManager;
import com.example.memberservice.repository.OrgManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerDetailsServiceImpl implements UserDetailsService {

  private final OrgManagerRepository orgManagerRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    OrgManager manager = orgManagerRepository.findByEmailOrElseThrow(username);

    return new ManagerDetailsImpl(manager, manager.getEmail());
  }
}