package com.example.contentservice.news.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsRequestDto {
	private String title;
	private String link;
	private String pubDate;

	public NewsRequestDto(String title, String link, String pubDate) {
		this.title = title;
		this.link = link;
		this.pubDate = pubDate;
	}
}