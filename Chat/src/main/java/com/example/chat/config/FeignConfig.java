package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;

/**
 * <ul>
 * <li>packageName    : com.example.chat.config
 * <li>fileName       : FeignConfig
 * <li>date           : 26. 2. 23.
 * <li>description    :
 * </ul>
 */
@Configuration
public class FeignConfig {
	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes != null) {
				// 1. 현재 브라우저/Postman에서 온 요청 헤더에서 Authorization 추출
				String authHeader = attributes.getRequest().getHeader("Authorization");

				// 2. MemberService로 나가는 Feign 요청 헤더에 삽입
				if (authHeader != null) {
					requestTemplate.header("Authorization", authHeader);
				}
			}
		};
	}
}
