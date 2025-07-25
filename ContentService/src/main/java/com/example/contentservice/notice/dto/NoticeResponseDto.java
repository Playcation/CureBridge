package com.example.contentservice.notice.dto;

import java.time.LocalDateTime;

import com.example.contentservice.notice.entity.Notice;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NoticeResponseDto {
	private Long noticeId;
	private Long userId;
	private String title;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static NoticeResponseDto toDto(Notice notice) {
		return NoticeResponseDto.builder()
			.noticeId(notice.getId())
			.userId(notice.getUserId())
			.title(notice.getTitle())
			.content(notice.getContent())
			.createdAt(notice.getCreatedAt())
			.updatedAt(notice.getUpdatedAt())
			.build();
	}
}
