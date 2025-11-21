package com.example.commonmodule.config;

public final class TokenSettings {

	// public static final long ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000L; // 10분
	// TODO: 테스트를 위해 임시로 access token 유효 시간을 24시간으로 설정함!!
	public static final long ACCESS_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 24분
	public static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 24시간
	public static final int COOKIE_EXPIRATION = 24 * 60 * 60; // 24시간

	public static final String ACCESS_TOKEN_CATEGORY = "Authorization";
	public static final String TOKEN_ISSUER = "curebridge";
	public static final String REFRESH_TOKEN_CATEGORY = "refresh";
}
