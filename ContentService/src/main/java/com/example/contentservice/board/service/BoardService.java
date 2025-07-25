package com.example.contentservice.board.service;

import java.util.List;

import com.example.contentservice.board.dto.BoardRequestDto;
import com.example.contentservice.board.dto.BoardResponseDto;
import com.example.contentservice.board.entity.BoardType;

public interface BoardService {
	BoardResponseDto createBoard(BoardRequestDto dto);

	BoardResponseDto getBoard(Long boardId);

	List<BoardResponseDto> getBoards(BoardType boardType);

	BoardResponseDto updateBoard(Long boardId, BoardRequestDto dto);

	void deleteBoard(Long boardId);
}