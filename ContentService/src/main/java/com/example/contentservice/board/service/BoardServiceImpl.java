package com.example.contentservice.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.contentservice.board.dto.BoardRequestDto;
import com.example.contentservice.board.dto.BoardResponseDto;
import com.example.contentservice.board.entity.Board;
import com.example.contentservice.board.entity.BoardType;
import com.example.contentservice.board.repository.BoardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;

	@Transactional
	public BoardResponseDto createBoard(BoardRequestDto requestDto, Long userId) {
		Board board = Board.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.boardType(requestDto.getBoardType())
			.userId(userId)
			.build();
		boardRepository.save(board);

		return BoardResponseDto.toDto(board);
	}

	public BoardResponseDto getBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("Board not found"));
		return BoardResponseDto.toDto(board);
	}

	public List<BoardResponseDto> getBoards(@RequestParam BoardType boardType) {
		return boardRepository.findAllByBoardType(boardType).stream()
			.map(BoardResponseDto::toDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public BoardResponseDto updateBoard(Long boardId, BoardRequestDto requestDto) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("Board not found"));
		board.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getBoardType());
		boardRepository.save(board);
		return BoardResponseDto.toDto(board);
	}

	@Transactional
	public void deleteBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("Board not found"));
		boardRepository.delete(board);
	}
}