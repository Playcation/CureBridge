package com.example.contentservice.notice.dto;

import java.time.LocalDateTime;

import com.example.contentservice.notice.document.NoticeDocument;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeSearchResponseDto {
	private Long id;
	private String title;
	private String content;
	private LocalDateTime createdAt;

	public static NoticeSearchResponseDto fromDocument(NoticeDocument doc) {
		return NoticeSearchResponseDto.builder()
			.id(Long.valueOf(doc.getId()))
			.title(doc.getTitle())
			.content(doc.getContent())
			.createdAt(doc.getCreatedAt())
			.build();
	}
}