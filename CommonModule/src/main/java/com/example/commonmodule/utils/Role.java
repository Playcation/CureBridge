package com.example.commonmodule.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO: Member 모듈에 있는 Role Common 모듈로 옮기기

@Getter
@RequiredArgsConstructor
public enum Role {
	// 높은 권한일수록 아래로 배치
	USER("USER"),
	ORG_MANAGER("ORG_MANAGER"),
	ORG_ADMIN("ORG_ADMIN"),
	ADMIN("ADMIN");

	private final String authority;

	// Authentication 객체에 맞추기 위해 GrantedAuthority 타입의 콜렉션으로 반환
	public Collection<? extends GrantedAuthority> toGrantedAuthorities() {
		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(this.getAuthority());
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);

		return authorities;
	}

	public boolean isHigherThanOrEqual(Role other) {
		return this.ordinal() >= other.ordinal();
	}

	/**
	 *
	 * @return Role 을 ROLE_name 형식의 문자열로 반환
	 */
	public String toSpringRole() {
		return "ROLE_" + this.name();
	}

	/**
	 *
	 * @param authority 문자열 형태의 권한
	 * @return ROLE_ 이 포함된 문자열 형태의 권한을 enum 으로 변환
	 */
	public static Optional<Role> fromAuthority(String authority) {
		if (authority.startsWith("ROLE_")) {
			try {
				return Optional.of(Role.valueOf(authority.replace("ROLE_", "")));
			} catch (IllegalArgumentException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
}
