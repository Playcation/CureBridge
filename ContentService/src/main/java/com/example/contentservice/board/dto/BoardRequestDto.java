package com.example.contentservice.board.dto;

import com.example.contentservice.board.entity.BoardType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
	private String title;
	private String content;
	private BoardType boardType;

	public BoardRequestDto(String title, String content, BoardType boardType, Long userId) {
		this.title = title;
		this.content = content;
		this.boardType = boardType;
	}
}