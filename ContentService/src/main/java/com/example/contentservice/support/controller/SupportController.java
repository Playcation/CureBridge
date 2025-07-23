package com.example.contentservice.support.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import com.example.contentservice.support.service.SupportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/support")
public class SupportController {
	private final SupportService supportService;

	// 게시물 등록
	@PostMapping
	public ResponseEntity<SupportResponseDto> createSupport(@RequestBody SupportRequestDto requestDto,
		@RequestParam Long userId) {
		SupportResponseDto responseDto = supportService.createSupport(requestDto, userId);
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}
}
