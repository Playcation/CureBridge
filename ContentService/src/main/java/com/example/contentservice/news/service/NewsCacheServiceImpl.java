package com.example.contentservice.news.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.dto.TopKeywordCacheDto;
import com.example.contentservice.news.dto.TopKeywordResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCacheServiceImpl implements NewsCacheService {

	private final CacheManager cacheManager;
	private final NewsSearchService newsSearchService; // 비즈니스 로직 호출용

	@Override
	public List<TopKeywordResponseDto> getCachedTopKeywords(LocalDate gte, LocalDate lt, int size) {
		Cache cache = cacheManager.getCache("news_top_keywords");
		if (cache != null) {
			TopKeywordCacheDto wrapper = cache.get("daily_top10", TopKeywordCacheDto.class);
			if (wrapper != null) {
				log.info(">>>> Redis 캐시 히트: 인기 키워드 반환");
				return wrapper.getKeywords();
			}
		}

		log.info(">>>> 캐시 미스: ES 직접 집계 실행");
		List<TopKeywordResponseDto> top10 = newsSearchService.aggregateTopKeywordsForDateRange(gte, lt, size);

		if (cache != null) {
			cache.put("daily_top10", new TopKeywordCacheDto(top10));
		}
		return top10;
	}

	@Override
	public void refreshNewsCache(LocalDate today) {
		LocalDate yesterday = today.minusDays(1);
		Cache keywordCache = cacheManager.getCache("news_top_keywords");
		Cache resultCache = cacheManager.getCache("news_keyword_results");

		if (keywordCache != null && resultCache != null) {
			// 인기 키워드 추출
			List<TopKeywordResponseDto> top10 = newsSearchService.aggregateTopKeywordsForDateRange(yesterday, today,
				50);
			keywordCache.put("daily_top10", new TopKeywordCacheDto(top10));

			// 각 키워드별 첫 페이지 미리 캐싱
			for (TopKeywordResponseDto keywordDto : top10) {
				String keyword = keywordDto.getKeyword();
				PagingDto<NewsResponseDto> results = newsSearchService.searchByTitle(keyword, PageRequest.of(0, 10));
				resultCache.put(keyword, results);
			}
			log.info(">>>> 뉴스 캐시 갱신 완료: {}", today);
		}
	}
}