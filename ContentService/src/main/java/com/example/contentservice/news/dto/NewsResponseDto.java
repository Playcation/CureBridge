package com.example.contentservice.news.dto;

import java.time.LocalDateTime;

import com.example.contentservice.news.entity.News;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsResponseDto {
	private Long id;
	private String title;
	private String link;
	private LocalDateTime publishedAt;

	public static NewsResponseDto toDto(News news) {
		return NewsResponseDto.builder()
			.id(news.getId())
			.title(news.getTitle())
			.link(news.getLink())
			.publishedAt(news.getPublishedAt())
			.build();
	}
}