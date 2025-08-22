package com.example.memberservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.UserErrorCode;
import com.example.commonmodule.files.service.FileService;
import com.example.memberservice.dto.MessageResponseDto;
import com.example.memberservice.dto.PwUpdateRequestDto;
import com.example.memberservice.dto.SignUpRequestDto;
import com.example.memberservice.dto.UpdateUserRequestDto;
import com.example.memberservice.dto.UpdateUserResponseDto;
import com.example.memberservice.dto.UserResponseDto;
import com.example.memberservice.entity.Patient;
import com.example.memberservice.entity.Role;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.PatientRepository;
import com.example.memberservice.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PatientRepository patientRepository;
	private final PasswordEncoder bCryptPasswordEncoder;

	// 회원가입
	// TODO: Util에 메시지만 전달하는 ResponseDto 추가?
	@Transactional
	public MessageResponseDto signUp(MultipartFile file, SignUpRequestDto dto) {

		// 비밀번호 암호화
		String pw = bCryptPasswordEncoder.encode(dto.getPassword());

		// 파일 저장
		if (file != null) {
			// TODO: S3에 파일 저장?
		}

		// TODO: Role 이 현재는 USER 로만 저장하는 중.
		User user = new User(dto.getEmail(), pw, dto.getName(), Role.USER, dto.getPhoneNumber(), dto.getBirthDate());
		User savedUser = userRepository.save(user);

		return new MessageResponseDto("회원가입 성공");
	}

	// 비밀번호 확인
	public MessageResponseDto checkPassword(Long userId, String input) {
		User findUser = userRepository.findByIdOrElseThrow(userId);
		if (checkPassword(input, findUser.getPassword())) {
			return new MessageResponseDto("비밀번호 확인 성공");
		} else {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}
	}

	// 비밀번호 확인 서비스단 메서드
	private boolean checkPassword(String inputPassword, String currPassword) {
		return bCryptPasswordEncoder.matches(inputPassword, currPassword);
	}

	// 현재 로그인 한 유저 검색
	public UserResponseDto findUser(Long userId) {
		User findUser = userRepository.findByIdOrElseThrow(userId);

		return findUser.toDto();
	}

	// 회원 정보 수정 (Patient의 sick만 수정)
	public UpdateUserResponseDto updateUser(Long userId, UpdateUserRequestDto dto) {

		if (dto.getSick().isEmpty()) {
			throw new InvalidInputException(UserErrorCode.EMPTY_INPUT_FIELDS);
		}
		Patient findPatient = patientRepository.findPatientByUserId(userId);
		findPatient.updatePatient(dto.getSick());

		return new UpdateUserResponseDto("회원 정보 수정이 완료되었습니다.", dto.getSick());
	}

	// 비밀번호 변경
	public MessageResponseDto updatePassword(Long userId, PwUpdateRequestDto dto) {
		User findUser = userRepository.findByIdOrElseThrow(userId);

		if (checkPassword(dto.getCurrPassword(), findUser.getPassword())) {
			if (dto.getCurrPassword().equals(dto.getNewPassword())) {
				throw new InvalidInputException(UserErrorCode.INVALID_PASSWORD);
			}
			String newPassword = bCryptPasswordEncoder.encode(dto.getNewPassword());
			findUser.updatePassword(newPassword);
			return new MessageResponseDto("비밀번호가 변경되었습니다.");
		} else {
			throw new NoAuthorizedException(UserErrorCode.NO_AUTHORIZED_PASSWORD);
		}
	}

	// 리프레시 토큰 발급용, id로 유저 정보 반환
	public User findUserById(Long id) {
		return userRepository.findByIdOrElseThrow(id);
	}

	// TODO: 유저 삭제 절차 결정, 현재는 deletedAt 만 설정중.
	public MessageResponseDto deleteUser(Long userId) {
		User findUser = userRepository.findByIdOrElseThrow(userId);
		// TODO: 이 부분 Protected 라고 오류 떠요
		// findUser.delete();
		userRepository.save(findUser);
		return new MessageResponseDto("유저 삭제 요청이 완료되었습니다. 30일 후 완전히 삭제됩니다.");
	}
}
