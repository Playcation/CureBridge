package com.example.contentservice.news.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@ExtendWith(MockitoExtension.class)
class NewsSearchServiceImplTest {

	@Mock
	private ElasticsearchClient elasticsearchClient;

	@InjectMocks
	private NewsSearchServiceImpl newsSearchService;

	@Test
	@DisplayName("한국어 포함 여부 정규식 유틸 테스트")
	void containsKorean_Validations_Test() {
		assertTrue(newsSearchService.containsKorean("의사"));
		assertTrue(newsSearchService.containsKorean("의학뉴스 10"));
		assertFalse(newsSearchService.containsKorean("Medical"));
		assertFalse(newsSearchService.containsKorean("123456"));
	}

	@Test
	@DisplayName("오래된 뉴스 청소 - 날짜 미만 조건 쿼리 전송 흐름 검증")
	void deleteOldNews_Execution_Test() throws Exception {
		// Given
		int days = 15;

		// When
		newsSearchService.deleteOldNews(days);

		// Then
		verify(elasticsearchClient, times(1)).deleteByQuery(any(java.util.function.Function.class));
	}
}