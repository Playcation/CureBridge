package com.example.contentservice.support.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SupportRequestDto {
	private String title;
	private String content;
	private boolean isReplied;

	public SupportRequestDto(String title, String content, boolean isReplied) {
		this.title = title;
		this.content = content;
		this.isReplied = isReplied;
	}
}