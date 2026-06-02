package com.example.contentservice.news.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.dto.TopKeywordCacheDto;
import com.example.contentservice.news.dto.TopKeywordResponseDto;

@ExtendWith(MockitoExtension.class)
class NewsCacheServiceImplTest {

	@Mock
	private CacheManager cacheManager;

	@Mock
	private Cache mockCache;

	@Mock
	private NewsSearchService newsSearchService;

	@InjectMocks
	private NewsCacheServiceImpl newsCacheService;

	@Test
	@DisplayName("인기 키워드 조회 - 캐시 히트 시 Redis에서 상위 키워드 즉시 반환")
	void getCachedTopKeywords_CacheHit_Test() {
		// Given
		LocalDate gte = LocalDate.now().minusDays(7);
		LocalDate lt = LocalDate.now().plusDays(1);
		String cacheKey = "daily_top10_" + LocalDate.now();

		TopKeywordResponseDto keywordDto = new TopKeywordResponseDto("의학", 15L);
		TopKeywordCacheDto cacheWrapper = new TopKeywordCacheDto(List.of(keywordDto));

		when(cacheManager.getCache("news_top_keywords")).thenReturn(mockCache);
		when(mockCache.get(cacheKey, TopKeywordCacheDto.class)).thenReturn(cacheWrapper);

		// When
		List<TopKeywordResponseDto> result = newsCacheService.getCachedTopKeywords(gte, lt, 10);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("의학", result.get(0).getKeyword());
		assertEquals(15L, result.get(0).getCount());

		verify(newsSearchService, never()).aggregateTopKeywordsForDateRange(any(), any(), anyInt());
	}

	@Test
	@DisplayName("인기 키워드 조회 - 캐시 미스 시 ES 집계 호출 및 결과 Redis에 캐싱")
	void getCachedTopKeywords_CacheMiss_Test() {
		// Given
		LocalDate gte = LocalDate.now().minusDays(7);
		LocalDate lt = LocalDate.now().plusDays(1);
		String cacheKey = "daily_top10_" + LocalDate.now();

		TopKeywordResponseDto dbResult = new TopKeywordResponseDto("코로나", 25L);
		List<TopKeywordResponseDto> esList = List.of(dbResult);

		when(cacheManager.getCache("news_top_keywords")).thenReturn(mockCache);
		// 캐시에 데이터가 없는 상태 (미스)
		when(mockCache.get(cacheKey, TopKeywordCacheDto.class)).thenReturn(null);
		when(newsSearchService.aggregateTopKeywordsForDateRange(gte, lt, 10)).thenReturn(esList);

		// When
		List<TopKeywordResponseDto> result = newsCacheService.getCachedTopKeywords(gte, lt, 10);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("코로나", result.get(0).getKeyword());

		verify(newsSearchService, times(1)).aggregateTopKeywordsForDateRange(gte, lt, 10);
		verify(mockCache, times(1)).put(eq(cacheKey), any(TopKeywordCacheDto.class));
	}

	@Test
	@DisplayName("자정 주기 캐시 갱신 - 상위 키워드 캐싱 및 각 키워드별 첫 페이지 검증")
	void refreshNewsCache_Success_Test() {
		// Given
		LocalDate today = LocalDate.now();
		Cache mockKeywordCache = mock(Cache.class);
		Cache mockResultCache = mock(Cache.class);

		when(cacheManager.getCache("news_top_keywords")).thenReturn(mockKeywordCache);
		when(cacheManager.getCache("news_keyword_results")).thenReturn(mockResultCache);

		TopKeywordResponseDto top1 = new TopKeywordResponseDto("헬스케어", 50L);
		List<TopKeywordResponseDto> top10 = List.of(top1);

		when(newsSearchService.aggregateTopKeywordsForDateRange(today.minusDays(7), today.plusDays(1), 50)).thenReturn(
			top10);

		PagingDto<NewsResponseDto> mockPagingResult = new PagingDto<>(List.of(), 0L);
		when(newsSearchService.searchByTitle("헬스케어", PageRequest.of(0, 10))).thenReturn(mockPagingResult);

		// When
		newsCacheService.refreshNewsCache(today);

		// Then
		String expectedCacheKey = "daily_top10_" + today;

		verify(mockKeywordCache, times(1)).put(eq(expectedCacheKey), any(TopKeywordCacheDto.class));
		verify(mockResultCache, times(1)).put("헬스케어", mockPagingResult);
	}
}