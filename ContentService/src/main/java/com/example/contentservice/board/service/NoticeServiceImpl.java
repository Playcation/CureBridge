package com.example.contentservice.board.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.board.dto.BoardRequestDto;
import com.example.contentservice.board.dto.BoardResponseDto;
import com.example.contentservice.board.entity.Board;
import com.example.contentservice.board.entity.BoardType;
import com.example.contentservice.board.repository.BoardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements BoardService {

	private final BoardRepository boardRepository;

	@Transactional
	public BoardResponseDto createBoard(BoardRequestDto requestDto, Long userId, BoardType boardType) {

		Board board = Board.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.boardType(boardType)
			.userId(userId)
			.build();
		boardRepository.save(board);

		return BoardResponseDto.toDto(board);
	}

	public BoardResponseDto getBoard(Long boardId) {
		Board board = boardRepository.findByIdOrElseThrow(boardId);
		return BoardResponseDto.toDto(board);
	}

	public PagingDto<BoardResponseDto> getBoardsAndPaging(int page, @RequestParam BoardType boardType) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
		Page<Board> boardPage = boardRepository.findAllByBoardType(boardType, pageable);

		List<BoardResponseDto> boardDtoList = boardPage.getContent().stream()
			.map(BoardResponseDto::toDto)
			.toList();

		return new PagingDto<>(boardDtoList, boardPage.getTotalElements());
	}

	@Transactional
	public BoardResponseDto updateBoard(Long boardId, BoardRequestDto requestDto) {
		Board board = boardRepository.findByIdOrElseThrow(boardId);
		board.update(requestDto.getTitle(), requestDto.getContent());
		boardRepository.save(board);
		return BoardResponseDto.toDto(board);
	}

	@Transactional
	public void deleteBoard(Long boardId) {
		Board board = boardRepository.findByIdOrElseThrow(boardId);
		board.delete();
		boardRepository.save(board);
	}
}