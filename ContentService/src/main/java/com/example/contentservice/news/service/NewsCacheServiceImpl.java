package com.example.contentservice.news.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.dto.TopKeywordCacheDto;
import com.example.contentservice.news.dto.TopKeywordResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 캐싱 관련 funtions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCacheServiceImpl implements NewsCacheService {

	private final CacheManager cacheManager;
	private final NewsSearchService newsSearchService; // 비즈니스 로직 호출용
	private final NewsService newsService;

	/**
	 * 1일 주기로 뉴스 데이터 갱신 시 캐시 갱신
	 * @param today
	 */
	@Override
	public void refreshNewsCache(LocalDate today) {
		LocalDate startDate = today.minusDays(7);
		LocalDate endDate = today.minusDays(1);
		String cacheKey = "daily_top10_" + startDate.toString();
		Cache keywordCache = cacheManager.getCache("news_top_keywords");
		Cache resultCache = cacheManager.getCache("news_keyword_results");

		if (keywordCache != null && resultCache != null) {
			// 인기 키워드 추출
			List<TopKeywordResponseDto> top10 = newsSearchService.aggregateTopKeywordsForDateRange(endDate, today,
				50);
			keywordCache.put(cacheKey, new TopKeywordCacheDto(top10));

			// 각 키워드별 첫 페이지 미리 캐싱
			for (TopKeywordResponseDto keywordDto : top10) {
				String keyword = keywordDto.getKeyword();
				PagingDto<NewsResponseDto> results = newsSearchService.searchByTitle(keyword, PageRequest.of(0, 10));
				resultCache.put(keyword, results);
			}
			log.info(">>> 뉴스 캐시 갱신 완료: {}", today);
		}
	}

	/**
	 * 인기 키워드 API 실행 시
	 * 캐시 탐색 후 있으면 가져오고 없으면 es 직접 집계
	 * @param gte
	 * @param lt
	 * @param size
	 * @return 키워드 탑10
	 */
	@Override
	public List<TopKeywordResponseDto> getCachedTopKeywords(LocalDate gte, LocalDate lt, int size) {
		String cacheKey = "daily_top10_" + gte.toString();
		Cache cache = cacheManager.getCache("news_top_keywords");
		if (cache != null) {    // 캐시 있으면 가져옴
			TopKeywordCacheDto wrapper = cache.get(cacheKey, TopKeywordCacheDto.class);
			if (wrapper != null) {
				log.info(">>>> Redis 캐시 히트: 인기 키워드 반환");
				return wrapper.getKeywords();
			}
		}

		log.info(">>>> 캐시 미스: ES 직접 집계 실행");
		List<TopKeywordResponseDto> top10 = newsSearchService.aggregateTopKeywordsForDateRange(gte, lt, size);

		if (cache != null) {
			cache.put(cacheKey, new TopKeywordCacheDto(top10));
		}
		return top10;
	}

	@Transactional
	public void saveRecentNewsAndRefreshCache(List<NewsRequestDto> dtoList, LocalDate today) {
		// 1. MySQL 저장 (이게 실패하면 전체 롤백)
		newsService.saveRecentNews(dtoList);

		// 2. Redis 캐시 갱신 (실패해도 MySQL은 살려야 함)
		try {
			refreshNewsCache(today);
		} catch (Exception e) {
			// 로그만 남기고 예외를 밖으로 던지지 않음 (중요!)
			log.error("Redis 캐시 갱신 중 오류 발생. 데이터는 MySQL에 정상 저장됨 : {}", e.getMessage());
		}
	}

}