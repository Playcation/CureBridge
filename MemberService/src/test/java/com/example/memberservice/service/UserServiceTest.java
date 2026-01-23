package com.example.memberservice.service;

import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.UserErrorCode;
import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.service.FileService;
import com.example.memberservice.dto.MessageResponseDto;
import com.example.memberservice.dto.PwUpdateRequestDto;
import com.example.memberservice.dto.SignUpRequestDto;
import com.example.memberservice.entity.Role;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private FileService fileService;
	private UserService userService;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		passwordEncoder = mock(PasswordEncoder.class);
		fileService = mock(FileService.class);
		userService = new UserService(userRepository, passwordEncoder, fileService);
	}

	@Test
	@DisplayName("회원가입 성공 테스트 - 파일 업로드 mock 처리")
	void signUp_success() {
		// given
		SignUpRequestDto dto = new SignUpRequestDto("test@test.com", "1234", "홍길동", "01012345678", LocalDate.of(1990,1,1));
		MultipartFile file = mock(MultipartFile.class);

		when(passwordEncoder.encode("1234")).thenReturn("encodedPw");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		FileResponseDto mockResponse = new FileResponseDto();
		when(fileService.uploadFile(file)).thenReturn(mockResponse);

		// when
		MessageResponseDto response = userService.signUp(file, dto);

		// then
		verify(fileService, times(1)).uploadFile(file);
		verify(userRepository, times(1)).save(any(User.class));
		assertThat(response.getMessage()).isEqualTo("회원가입 성공");
	}


	@Test
	@DisplayName("비밀번호 확인 성공")
	void checkPassword_success() {
		User user = new User("test@test.com", "encodedPw", "홍길동", Role.USER, "01012345678", LocalDate.of(1990,1,1));
		when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);
		when(passwordEncoder.matches("1234", "encodedPw")).thenReturn(true);

		MessageResponseDto response = userService.checkPassword(1L, "1234");

		assertThat(response.getMessage()).isEqualTo("비밀번호 확인 성공");
	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void updatePassword_success() {
		User user = new User("test@test.com", "encodedPw", "홍길동", Role.USER, "01012345678", LocalDate.of(1990,1,1));
		when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);
		when(passwordEncoder.matches("currPw", "encodedPw")).thenReturn(true);
		when(passwordEncoder.encode("newPw")).thenReturn("encodedNewPw");

		PwUpdateRequestDto dto = new PwUpdateRequestDto("currPw", "newPw");
		MessageResponseDto response = userService.updatePassword(1L, dto);

		assertThat(response.getMessage()).isEqualTo("비밀번호가 변경되었습니다.");
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 현재 비밀번호와 동일")
	void updatePassword_fail_samePassword() {
		User user = new User("test@test.com", "encodedPw", "홍길동", Role.USER, "01012345678", LocalDate.of(1990,1,1));
		when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);
		when(passwordEncoder.matches("samePw", "encodedPw")).thenReturn(true);

		PwUpdateRequestDto dto = new PwUpdateRequestDto("samePw", "samePw");

		assertThatThrownBy(() -> userService.updatePassword(1L, dto))
			.isInstanceOf(InvalidInputException.class)
			.hasMessageContaining(UserErrorCode.INVALID_PASSWORD.getMessage());
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 인증 실패")
	void updatePassword_fail_noAuth() {
		User user = new User("test@test.com", "encodedPw", "홍길동", Role.USER, "01012345678", LocalDate.of(1990,1,1));
		when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);
		when(passwordEncoder.matches("wrongPw", "encodedPw")).thenReturn(false);

		PwUpdateRequestDto dto = new PwUpdateRequestDto("wrongPw", "newPw");

		assertThatThrownBy(() -> userService.updatePassword(1L, dto))
			.isInstanceOf(NoAuthorizedException.class)
			.hasMessageContaining(UserErrorCode.NO_AUTHORIZED_PASSWORD.getMessage());
	}

	@Test
	@DisplayName("유저 삭제 테스트")
	void deleteUser_success() {
		User user = new User("test@test.com", "encodedPw", "홍길동", Role.USER, "01012345678", LocalDate.of(1990,1,1));
		when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);

		MessageResponseDto response = userService.deleteUser(1L);

		verify(userRepository, times(1)).save(user);
		assertThat(response.getMessage()).contains("유저 삭제 요청이 완료되었습니다");
	}
}
