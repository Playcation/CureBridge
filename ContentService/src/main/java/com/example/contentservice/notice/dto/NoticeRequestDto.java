package com.example.contentservice.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {
	private String title;
	private String content;

	public NoticeRequestDto(String title, String content) {
		this.title = title;
		this.content = content;
	}
}