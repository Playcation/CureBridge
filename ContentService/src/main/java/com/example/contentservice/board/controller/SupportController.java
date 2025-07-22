package com.example.contentservice.board.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.contentservice.board.dto.SupportRequestDto;
import com.example.contentservice.board.dto.SupportResponseDto;
import com.example.contentservice.board.entity.BoardType;
import com.example.contentservice.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/support")
public class SupportController {
	private final BoardService<SupportRequestDto, SupportResponseDto> boardService;

	// 게시물 등록
	@PostMapping
	public ResponseEntity<SupportResponseDto> createBoard(@RequestBody SupportRequestDto requestDto,
		@RequestParam Long userId) {
		SupportResponseDto responseDto = boardService.createBoard(requestDto, userId, BoardType.SUPPORT);
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}
}
