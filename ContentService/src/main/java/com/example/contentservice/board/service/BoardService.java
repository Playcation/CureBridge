package com.example.contentservice.board.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.board.dto.BoardRequestDto;
import com.example.contentservice.board.dto.BoardResponseDto;
import com.example.contentservice.board.entity.BoardType;

public interface BoardService<T extends BoardRequestDto, R extends BoardResponseDto> {
	R createBoard(T dto, Long userId, BoardType boardType);

	R getBoard(Long boardId);

	PagingDto<R> getBoardsAndPaging(int page, BoardType boardType);

	R updateBoard(Long boardId, T dto);

	void deleteBoard(Long boardId);
}