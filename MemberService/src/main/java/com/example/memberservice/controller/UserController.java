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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.memberservice.dto.MessageResponseDto;
import com.example.memberservice.dto.PwCheckRequestDto;
import com.example.memberservice.dto.PwUpdateRequestDto;
import com.example.memberservice.dto.SignUpRequestDto;
import com.example.memberservice.dto.UpdateUserRequestDto;
import com.example.memberservice.dto.UpdateUserResponseDto;
import com.example.memberservice.dto.UserResponseDto;
import com.example.memberservice.security.JwtUtil;
import com.example.memberservice.security.TokenSettings;
import com.example.memberservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	/**
	 * 회원가입
	 * <p>현재 별도의 인증 절차는 없음 (전화번호)</p>
	 *
	 * @param dto 저장해야 하는 회원 정보
	 * @return 성공시 계정이 생성되었다는 메시지
	 */
	@PostMapping("/auth/signup")
	public ResponseEntity<MessageResponseDto> signUp(
		@RequestPart(name = "profile")MultipartFile profile,
		 @Valid @RequestPart(name = "data") SignUpRequestDto dto
	) {
		MessageResponseDto messageResponseDto = userService.signUp(profile, dto);
		return new ResponseEntity<>(messageResponseDto, HttpStatus.CREATED);
	}

	/**
	 * 현재 로그인 한 유저의 비밀번호 인증 (본인인증용)
	 *
	 * @param authorizationHeader 토큰 정보
	 * @param dto 비밀번호
	 * @return 성공시 인증 성공 메시지
	 */
	@GetMapping("/check")
	public ResponseEntity<MessageResponseDto> checkPassword(
		@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
		@RequestBody PwCheckRequestDto dto
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		MessageResponseDto messageResponseDto = userService.checkPassword(userId, dto.getPassword());
		return new ResponseEntity<>(messageResponseDto, HttpStatus.OK);
	}

	// TODO: 전체 정보를 포함하는 요청과 공개 정보만 포함하는 요청을 따로 둘까요?

	/**
	 * param 의 id에 해당하는 유저의 정보 조회
	 *
	 * @param id 유저 id
	 * @param authorizationHeader 토큰 정보
	 * @return 현재 로그인 한 유저 정보
	 */
	@GetMapping
	public ResponseEntity<UserResponseDto> findUser(
		@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
		@RequestParam Long id
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		UserResponseDto userResponseDto = userService.findUser(userId);
		return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
	}

	/**
	 * 비밀번호 제외, 현재 로그인 한 유저 정보 수정
	 *
	 * @param authorizationHeader 토큰 정보
	 * @param dto 수정할 유저 정보 (질병)
	 * @return 수정 성공시 성공 메시지
	 */
	@PatchMapping
	public ResponseEntity<UpdateUserResponseDto> updateUser(
		@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
		@RequestBody UpdateUserRequestDto dto
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		UpdateUserResponseDto updateUserResponseDto = userService.updateUser(userId, dto);
		return new ResponseEntity<>(updateUserResponseDto, HttpStatus.OK);
	}

	/**
	 * 현 비밀번호를 확인한 후 비밀번호 변경
	 *
	 * @param authorizationHeader 토큰 정보
	 * @param dto 현 비밀번호, 변경할 비밀번호
	 * @return 비밀번호 인증 성공 -> 변경 완료시 성공 메시지
	 */
	@PatchMapping("/password")
	public ResponseEntity<?> updatePassword(
		@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
		@RequestBody PwUpdateRequestDto dto
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		MessageResponseDto messageResponseDto = userService.updatePassword(userId, dto);

		return new ResponseEntity<>(messageResponseDto, HttpStatus.OK);
	}

	/**
	 * 회원 탈퇴 메서드
	 * <p>!! 호출 전 반드시 비밀번호 인증 거치기 !!</p>
	 *
	 * @param authorizationHeader 토큰 정보
	 * @return 성공시 성공 알림 메시지
	 */
	@DeleteMapping
	public ResponseEntity<MessageResponseDto> deleteUser(
		@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
	) {
		Long userId = jwtUtil.findUserByToken(authorizationHeader);
		MessageResponseDto messageResponseDto = userService.deleteUser(userId);

		return new ResponseEntity<>(messageResponseDto, HttpStatus.OK);
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
