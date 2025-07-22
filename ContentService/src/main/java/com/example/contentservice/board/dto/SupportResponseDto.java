package com.example.contentservice.board.dto;

import java.time.LocalDateTime;

import com.example.contentservice.board.entity.Board;
import com.example.contentservice.board.entity.BoardType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SupportResponseDto extends BoardResponseDto {

	private final boolean isPrivate;

	@Builder(builderMethodName = "supportBuilder")
	public SupportResponseDto(Long boardId, Long userId, String title, String content, BoardType boardType,
		LocalDateTime createdAt, LocalDateTime updatedAt, boolean isPrivate) {
		super(boardId, userId, title, content, boardType, createdAt, updatedAt);
		this.isPrivate = isPrivate;
	}

	public static SupportResponseDto toDto(Board board) {
		return SupportResponseDto.supportBuilder()
			.boardId(board.getId())
			.userId(board.getUserId())
			.title(board.getTitle())
			.content(board.getContent())
			.boardType(board.getBoardType())
			.createdAt(board.getCreatedAt())
			.updatedAt(board.getUpdatedAt())
			.isPrivate(board.isPrivate()) // 엔티티에 isPrivate 필드가 있다고 가정
			.build();
	}
}
