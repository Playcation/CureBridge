package com.example.contentservice.news.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopKeywordCacheDto {
	private List<TopKeywordResponseDto> keywords;
}