package com.example.contentservice.support.dto;

import java.time.LocalDateTime;

import com.example.contentservice.support.entity.Support;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupportResponseDto {
	private Long supportId;
	private Long userId;
	private String title;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static SupportResponseDto toDto(Support support) {
		return SupportResponseDto.builder()
			.supportId(support.getId())
			.userId(support.getUserId())
			.title(support.getTitle())
			.content(support.getContent())
			.createdAt(support.getCreatedAt())
			.updatedAt(support.getUpdatedAt())
			.build();
	}

}
