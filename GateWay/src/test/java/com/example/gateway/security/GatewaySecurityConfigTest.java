package com.example.gateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import javax.crypto.SecretKey;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class GatewaySecurityConfigTest {

	@Autowired
	private WebTestClient webTestClient;

	// 테스트용 SecretKey 빈 등록
	@TestConfiguration
	static class TestConfig {
		@Bean
		public SecretKey secretKey() {
			return new javax.crypto.spec.SecretKeySpec(
				"your-very-long-secret-key-more-than-32-bytes".getBytes(), "HmacSHA384");
		}
	}

	@Test
	@DisplayName("1. 인증 없는 사용자 - 익명 허용 엔드포인트(/api/anonymous/**) 접근 시 보안 통과")
	void anonymousUser_CanAccess_AnonymousEndpoint() {
		webTestClient.get()
			.uri("/api/anonymous/member/test")
			.exchange()
			// 보안 필터를 통과했지만 백엔드(8081)가 켜져있지 않아 라우팅 실패로 503/404 발생
			// 중요한 것은 401(Unauthorized)이나 403(Forbidden)이 아니라는 점입니다.
			.expectStatus().is5xxServerError();
	}

	@Test
	@DisplayName("2. 인증 없는 사용자 - 보호된 엔드포인트(/api/user/**) 접근 시 401 에러")
	void unauthenticatedUser_CannotAccess_ProtectedEndpoint() {
		webTestClient.get()
			.uri("/api/user/member/1")
			.exchange()
			.expectStatus().isUnauthorized(); // 401
	}

	@Test
	@DisplayName("3. USER 권한 - USER 엔드포인트 접근 시 보안 통과")
	void userRole_CanAccess_UserEndpoint() {
		webTestClient
			// mockJwt()를 사용하여 가짜 JWT 토큰에 "roles" 클레임을 주입합니다.
			.mutateWith(mockJwt().jwt(jwt -> jwt.claim("roles", "USER")))
			.get()
			.uri("/api/user/member/1")
			.exchange()
			// 보안 통과 여부 확인 (401, 403이 아님을 확인)
			.expectStatus().is5xxServerError();
	}

	@Test
	@DisplayName("4. USER 권한 - 상위 권한(ADMIN) 엔드포인트 접근 시 403 에러")
	void userRole_CannotAccess_AdminEndpoint() {
		webTestClient
			.mutateWith(mockJwt().jwt(jwt -> jwt.claim("roles", "USER")))
			.get()
			.uri("/api/admin/member/1")
			.exchange()
			.expectStatus().isForbidden(); // 403
	}

	@Test
	@DisplayName("5. ADMIN 권한 - 하위 권한(USER) 엔드포인트 접근 시 보안 통과 (hasRoleOrHigher 로직)")
	void adminRole_CanAccess_UserEndpoint() {
		webTestClient
			.mutateWith(mockJwt().jwt(jwt -> jwt.claim("roles", "ADMIN")))
			.get()
			.uri("/api/user/member/1")
			.exchange()
			// 상위 권한이므로 통과해야 함
			.expectStatus().is5xxServerError();
	}
}