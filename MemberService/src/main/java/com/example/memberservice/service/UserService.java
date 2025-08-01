package com.example.memberservice.service;

import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.memberservice.dto.MessageResponseDto;
import com.example.memberservice.dto.PwUpdateRequestDto;
import com.example.memberservice.dto.SignUpRequestDto;
import com.example.memberservice.dto.UpdateUserRequestDto;
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
	// TODO: Role 부분 enum으로 변경
	// 	Util에 메시지만 전달하는 ResponseDto 추가?
	@Transactional
	public MessageResponseDto signUp(SignUpRequestDto dto) {

		// 비밀번호 암호화
		String pw = bCryptPasswordEncoder.encode(dto.getPassword());

		// TODO: Role 종류 설정
		User user = new User(dto.getEmail(), pw, dto.getName(), Role.USER, dto.getPhoneNumber(), dto.getBirthDate());
		User savedUser = userRepository.save(user);

		log.info("[UserService] savedUser: {}", savedUser.getName());

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
	public MessageResponseDto updateUser(Long userId, UpdateUserRequestDto dto) {
		Patient findPatient = patientRepository.findPatientByUserId(userId);
		findPatient.updatePatient(dto.getSick());

		return new MessageResponseDto("회원 정보 수정이 완료되었습니다.");
	}

	// 비밀번호 변경
	public MessageResponseDto updatePassword(Long userId, PwUpdateRequestDto dto) {
		User findUser = userRepository.findByIdOrElseThrow(userId);

		if (checkPassword(dto.getCurrPassword(), findUser.getPassword())) {
			if (dto.getCurrPassword().equals(dto.getNewPassword())) {
				throw new RuntimeException("이전 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
			}
			String newPassword = bCryptPasswordEncoder.encode(dto.getNewPassword());
			findUser.updatePassword(newPassword);
			return new MessageResponseDto("비밀번호가 변경되었습니다.");
		} else {
			throw new RuntimeException("기존 비밀번호가 옳지 않습니다.");
		}
	}
}
