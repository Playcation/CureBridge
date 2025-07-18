package com.example.memberservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.memberservice.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// TODO: 회원가입
	@PostMapping("/signup")
	public ResponseEntity<?> signUp() {
		return null;
	}

	// TODO: 일반 로그인
	@GetMapping("/login")
	public ResponseEntity<?> login() {
		return null;
	}

	// TODO: 비밀번호 확인
	@GetMapping("/auth")
	public ResponseEntity<?> checkPassword() {
		return null;
	}

	// TODO: 유저 단일 조회
	@GetMapping
	public ResponseEntity<?> findUser() {
		return null;
	}

	// TODO: 유저 정보 수정
	@PatchMapping
	public ResponseEntity<?> updateUser() {
		return null;
	}

	// TODO: 유저 비밀번호 수정
	@PatchMapping("/password")
	public ResponseEntity<?> updatePassword() {
		return null;
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
