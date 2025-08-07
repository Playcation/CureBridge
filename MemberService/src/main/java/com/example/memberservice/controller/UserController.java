package com.example.memberservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.memberservice.dto.MessageResponseDto;
import com.example.memberservice.dto.PwCheckRequestDto;
import com.example.memberservice.dto.PwUpdateRequestDto;
import com.example.memberservice.dto.SignUpRequestDto;
import com.example.memberservice.dto.UpdateUserRequestDto;
import com.example.memberservice.dto.UserResponseDto;
import com.example.memberservice.security.JwtUtil;
import com.example.memberservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	// TODO: 회원가입
	// 	사진 올리는 경우 RequestPart로 수정 예정
	@PostMapping("/auth/signup")
	public ResponseEntity<MessageResponseDto> signUp(
		 @Valid @RequestBody SignUpRequestDto dto
	) {
		MessageResponseDto messageResponseDto = userService.signUp(dto);
		return new ResponseEntity<>(messageResponseDto, HttpStatus.CREATED);
	}

	// 비밀번호 확인
	@GetMapping("/auth")
	public ResponseEntity<MessageResponseDto> checkPassword(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody PwCheckRequestDto dto
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		MessageResponseDto messageResponseDto = userService.checkPassword(userId, dto.getPassword());
		return new ResponseEntity<>(messageResponseDto, HttpStatus.OK);
	}

	// TODO: 유저 단일 조회 (고민중...)
	// 	1. 현재 로그인 한 유저만 조회하고 다른 조회기능 admin에 넣기
	// 	2. param에 id 넣기
	@GetMapping
	public ResponseEntity<UserResponseDto> findUser(
		@RequestHeader("Authorization") String authorizationHeader
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		UserResponseDto userResponseDto = userService.findUser(userId);
		return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
	}

	// 유저 정보 수정
	@PatchMapping
	public ResponseEntity<?> updateUser(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody UpdateUserRequestDto dto
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		userService.updateUser(userId, dto);
		return null;
	}

	// 유저 비밀번호 수정
	@PatchMapping("/password")
	public ResponseEntity<?> updatePassword(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody PwUpdateRequestDto dto
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		MessageResponseDto messageResponseDto = userService.updatePassword(userId, dto);

		return new ResponseEntity<>(messageResponseDto, HttpStatus.OK);
	}

	// TODO: 회원 탈퇴
	@DeleteMapping
	public ResponseEntity<?> deleteUser() {
		return null;
	}

	// -------
	// 환자- 보호자
	// -------

	// TODO: 환자 등록 요청
	@PostMapping("/patients")
	public ResponseEntity<?> createRelation() {
		return null;
	}

	// TODO: 환자 등록 요청 수락
	@PatchMapping("/family")
	public ResponseEntity<?> acceptRelation() {
		return null;
	}

	// TODO: 환자-보호자 관계 조회
	@GetMapping("/relation")
	public ResponseEntity<?> findRelation() {
		return null;
	}

	// TODO: 환자-보호자 관계 삭제 요청
	@DeleteMapping("/relation")
	public ResponseEntity<?> deleteRelation() {
		return null;
	}
}
