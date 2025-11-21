package com.example.memberservice.security;

import com.example.memberservice.entity.Organization;
import com.example.memberservice.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationDetailsServiceImpl implements UserDetailsService {

  private final OrganizationRepository organizationRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Organization organization = organizationRepository.findByEmailOrElseThrow(username);

    return new OrganizationDetailsImpl(organization, organization.getEmail());
  }
}
