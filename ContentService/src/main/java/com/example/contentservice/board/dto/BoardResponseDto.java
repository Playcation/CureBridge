package com.example.contentservice.board.dto;

import java.time.LocalDateTime;

import com.example.contentservice.board.entity.Board;
import com.example.contentservice.board.entity.BoardType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardResponseDto {
	private Long boardId;
	private Long userId;
	private String title;
	private String content;
	private BoardType boardType;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static BoardResponseDto toDto(Board board) {
		return BoardResponseDto.builder()
			.boardId(board.getId())
			.userId(board.getUserId())
			.title(board.getTitle())
			.content(board.getContent())
			.boardType(board.getBoardType())
			.createdAt(board.getCreatedAt())
			.updatedAt(board.getUpdatedAt())
			.build();
	}
}
