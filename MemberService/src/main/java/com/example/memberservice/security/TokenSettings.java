package com.example.memberservice.security;

public final class TokenSettings {
	public static final String ACCESS_TOKEN_CATEGORY = "Authentication";
	public static final String TOKEN_ISSUER = "curebridge";
	public static final long ACCESS_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // TODO: 임시로 24시간
}
