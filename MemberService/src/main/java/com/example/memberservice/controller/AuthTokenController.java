package com.example.memberservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.memberservice.service.AuthTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthTokenController {

	private final AuthTokenService authTokenService;

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		authTokenService.createNewToken(request);

		return null;
	}
}
