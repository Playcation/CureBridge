package com.example.contentservice.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.contentservice.board.dto.BoardRequestDto;
import com.example.contentservice.board.dto.BoardResponseDto;
import com.example.contentservice.board.entity.BoardType;
import com.example.contentservice.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

	private final BoardService boardService;

	// 게시물 등록
	@PostMapping
	public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto requestDto) {
		BoardResponseDto responseDto = boardService.createBoard(requestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// 게시물 단건 조회
	@GetMapping("/{boardId}")
	public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long boardId) {
		BoardResponseDto responseDto = boardService.getBoard(boardId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	// 게시물 다건 조회
	@GetMapping
	public ResponseEntity<List<BoardResponseDto>> getBoards(@RequestParam BoardType boardType) {
		List<BoardResponseDto> boards = boardService.getBoards(boardType);
		return new ResponseEntity<>(boards, HttpStatus.OK);
	}

	// 게시물 수정
	@PutMapping("/{boardId}")
	public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId,
		@RequestBody BoardRequestDto requestDto) {
		BoardResponseDto responseDto = boardService.updateBoard(boardId, requestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@DeleteMapping("/{boardId}")
	public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
		boardService.deleteBoard(boardId);
		return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
	}
}