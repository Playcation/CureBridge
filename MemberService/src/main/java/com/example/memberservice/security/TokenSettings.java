package com.example.memberservice.security;

public final class TokenSettings {

	public static final long ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000L; // 10분
	public static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 24시간
	public static final int COOKIE_EXPIRATION = 24 * 60 * 60; // 24시간

	public static final String ACCESS_TOKEN_CATEGORY = "Authentication";
	public static final String TOKEN_ISSUER = "curebridge";
	public static final String REFRESH_TOKEN_CATEGORY = "refresh";
}
