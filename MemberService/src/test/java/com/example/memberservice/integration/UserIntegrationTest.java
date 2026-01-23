package com.example.memberservice.integration;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.memberservice.dto.MessageResponseDto;
import com.example.memberservice.dto.SignUpRequestDto;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.UserRepository;
import com.example.memberservice.service.UserService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test") // application-test.yml 사용
class UserIntegrationTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("회원가입 후 DB에 저장되는지 확인")
	void signUpIntegrationTest() {
		SignUpRequestDto dto = new SignUpRequestDto(
			"test@test.com",
			"1234",
			"홍길동",
			"01012345678",
			LocalDate.of(1990, 1, 1)
		);

		MessageResponseDto response = userService.signUp(null, dto);

		assertThat(response.getMessage()).isEqualTo("회원가입 성공");
		assertThat(userRepository.findUserByEmail("test@test.com")).isPresent();
	}

	@Test
	@DisplayName("회원 비밀번호 변경 통합 테스트")
	void updatePasswordIntegrationTest() {
		// 회원가입
		SignUpRequestDto dto = new SignUpRequestDto(
			"pwchange@test.com", "1234", "홍길동",
			"01011112222", LocalDate.of(1990, 1, 1)
		);
		userService.signUp(null, dto);

		// 비밀번호 변경
		User user = userRepository.findUserByEmail("pwchange@test.com").orElseThrow();
		user.updatePassword("encodedNewPw");
		userRepository.save(user);

		// 검증
		User updated = userRepository.findUserByEmail("pwchange@test.com").orElseThrow();
		assertThat(updated.getPassword()).isEqualTo("encodedNewPw");
	}

	@Test
	@DisplayName("회원 삭제 통합 테스트")
	void deleteUserIntegrationTest() {
		// 회원가입
		SignUpRequestDto dto = new SignUpRequestDto(
			"delete@test.com", "1234", "홍길동",
			"01033334444", LocalDate.of(1993, 3, 3)
		);
		userService.signUp(null, dto);

		// 삭제 처리
		User user = userRepository.findUserByEmail("delete@test.com").orElseThrow();
		user.delete();
		userRepository.save(user);

		// 검증
		User deleted = userRepository.findUserByEmail("delete@test.com").orElseThrow();
		assertThat(deleted.getDeletedAt()).isNotNull();
	}
}
