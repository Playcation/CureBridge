package com.example.contentservice.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SupportRequestDto extends BoardRequestDto {
	private boolean isPrivate;

	public SupportRequestDto(String title, String content, boolean isPrivate) {
		super(title, content);
		this.isPrivate = isPrivate;
	}
}