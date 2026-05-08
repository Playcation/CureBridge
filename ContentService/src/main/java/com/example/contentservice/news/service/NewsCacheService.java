package com.example.contentservice.news.service;

import java.time.LocalDate;
import java.util.List;

import com.example.contentservice.news.dto.TopKeywordResponseDto;

public interface NewsCacheService {
	// Redis에서 인기 키워드 조회 (Cache-Aside)
	List<TopKeywordResponseDto> getCachedTopKeywords(LocalDate gte, LocalDate lt, int size);

	// 스케줄러 등에 의해 캐시 강제 갱신
	void refreshNewsCache(LocalDate today);
}
