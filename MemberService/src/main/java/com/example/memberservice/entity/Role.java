package com.example.memberservice.entity;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 유저 권한 ENUM
@Getter
@RequiredArgsConstructor
public enum Role {
	USER("USER"),
	ADMIN("ADMIN");

	private final String authority;

	// Authentication 객체에 맞추기 위해 GrantedAuthority 타입의 콜렉션으로 반환
	public Collection<? extends GrantedAuthority> toGrantedAuthorities() {
		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(this.getAuthority());
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);

		return authorities;
	}
}
