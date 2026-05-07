package com.example.contentservice.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopKeywordResponseDto {

	private String keyword;
	private long count;
}