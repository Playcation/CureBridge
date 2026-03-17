package com.example.memberservice.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.commonmodule.utils.Role;
import com.example.memberservice.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

	private final User user;
	private final String username;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Role role = user.getRole();
		return role.toGrantedAuthorities();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.username;
	}
}
