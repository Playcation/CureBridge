package com.example.memberservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.memberservice.security.TokenSettings;
import com.example.memberservice.service.AuthTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthTokenController {

	private final AuthTokenService authTokenService;

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		String[] newTokens = authTokenService.createNewToken(request);

		response.setHeader(TokenSettings.ACCESS_TOKEN_CATEGORY, newTokens[0]);
		response.addCookie(authTokenService.getRefreshCookie(newTokens[1]));

		return new ResponseEntity<>("토큰이 발급되었습니다. : " + newTokens[0], HttpStatus.OK);
	}
}
