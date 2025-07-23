package com.example.contentservice.support.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SupportRequestDto {
	private String title;
	private String content;
	private boolean isPrivate;

	public SupportRequestDto(String title, String content, boolean isPrivate) {
		this.title = title;
		this.content = content;
		this.isPrivate = isPrivate;
	}
}