package com.example.contentservice.news.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.document.NewsDocument;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.entity.News;
import com.example.contentservice.news.repository.NewsRepository;
import com.example.contentservice.news.repository.NewsSearchRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

  @Mock
  private NewsRepository newsRepository;

  @Mock
  private NewsSearchRepository newsSearchRepository;

  @InjectMocks
  private NewsServiceImpl newsService;

  @Test
  @DisplayName("최근 24시간 이내 + 링크 중복 X -> DB 저장 + ES 저장")
  void saveRecentNews_Success_Test() {
    // Given
    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    // 서비스는 LocalDateTime.now() 기준으로 24시간 필터링하므로,
    // pubDate를 확실히 "최근" 시간대로 만들어줌
    String recentPubDate = ZonedDateTime.now(ZoneId.of("UTC"))
        .minusHours(1)
        .format(formatter);

    NewsRequestDto recentDto = new NewsRequestDto("title1", "https://news.com/1", recentPubDate);

    when(newsRepository.existsByLink("https://news.com/1")).thenReturn(false);

    // When
    newsService.saveRecentNews(List.of(recentDto));

    // Then - DB 저장 검증
    ArgumentCaptor<List<News>> newsCaptor = ArgumentCaptor.forClass(List.class);
    verify(newsRepository, times(1)).saveAll(newsCaptor.capture());

    List<News> savedNews = newsCaptor.getValue();
    assertEquals(1, savedNews.size());
    assertEquals("title1", savedNews.get(0).getTitle());
    assertEquals("https://news.com/1", savedNews.get(0).getLink());
    assertNotNull(savedNews.get(0).getPublishedAt());

    // Then - ES 저장 검증
    ArgumentCaptor<List<NewsDocument>> docCaptor = ArgumentCaptor.forClass(List.class);
    verify(newsSearchRepository, times(1)).saveAll(docCaptor.capture());
    assertEquals(1, docCaptor.getValue().size());
  }

  @Test
  @DisplayName("최근 24시간 이내라도 링크 중복이면 저장하지 않음")
  void saveRecentNews_DuplicatedLink_Test() {
    // Given
    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    String recentPubDate = ZonedDateTime.now(ZoneId.of("UTC"))
        .minusHours(2)
        .format(formatter);

    NewsRequestDto dto = new NewsRequestDto("dup", "https://news.com/dup", recentPubDate);

    when(newsRepository.existsByLink("https://news.com/dup")).thenReturn(true);

    // When
    newsService.saveRecentNews(List.of(dto));

    // Then
    ArgumentCaptor<List<News>> newsCaptor = ArgumentCaptor.forClass(List.class);
    verify(newsRepository, times(1)).saveAll(newsCaptor.capture());
    assertEquals(0, newsCaptor.getValue().size()); // 중복이면 posts가 빈 리스트로 저장됨

    ArgumentCaptor<List<NewsDocument>> docCaptor = ArgumentCaptor.forClass(List.class);
    verify(newsSearchRepository, times(1)).saveAll(docCaptor.capture());
    assertEquals(0, docCaptor.getValue().size());
  }

  @Test
  @DisplayName("최근 24시간 밖 데이터는 필터링되어 저장되지 않음")
  void saveRecentNews_OlderThan24Hours_Test() {
    // Given
    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    String oldPubDate = ZonedDateTime.now(ZoneId.of("UTC"))
        .minusHours(30)
        .format(formatter);

    NewsRequestDto oldDto = new NewsRequestDto("old", "https://news.com/old", oldPubDate);

    // When
    newsService.saveRecentNews(List.of(oldDto));

    // Then
    ArgumentCaptor<List<News>> newsCaptor = ArgumentCaptor.forClass(List.class);
    verify(newsRepository, times(1)).saveAll(newsCaptor.capture());
    assertEquals(0, newsCaptor.getValue().size());

    ArgumentCaptor<List<NewsDocument>> docCaptor = ArgumentCaptor.forClass(List.class);
    verify(newsSearchRepository, times(1)).saveAll(docCaptor.capture());
    assertEquals(0, docCaptor.getValue().size());

    // 시간 필터에서 먼저 걸러져야 하므로 existsByLink 호출 자체가 없어야 함
    verify(newsRepository, never()).existsByLink(any());
  }

  @Test
  @DisplayName("뉴스 페이징 조회 -> PagingDto 반환")
  void getNewsAndPaging_Test() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    News n1 = News.builder().title("t1").link("l1").build();
    News n2 = News.builder().title("t2").link("l2").build();

    when(newsRepository.findAll(eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(n1, n2), pageable, 2));

    // When
    PagingDto<NewsResponseDto> result = newsService.getNewsAndPaging(pageable);

    // Then
    assertNotNull(result);

    verify(newsRepository, times(1)).findAll(pageable);
  }

  @Test
  @DisplayName("뉴스 삭제 -> 존재 확인 후 deleteById 호출")
  void deleteNews_Test() {
    // Given
    Long newsId = 10L;
    when(newsRepository.findByIdOrElseThrow(newsId)).thenReturn(News.builder().title("x").build());

    // When
    newsService.deleteNews(newsId);

    // Then
    verify(newsRepository, times(1)).findByIdOrElseThrow(newsId);
    verify(newsRepository, times(1)).deleteById(newsId);
  }
}