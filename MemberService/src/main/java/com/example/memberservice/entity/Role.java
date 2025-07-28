package com.example.memberservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 유저 권한 ENUM
@Getter
@RequiredArgsConstructor
public enum Role {
	USER("USER"),
	ADMIN("ADMIN");

	private final String authority;
}
