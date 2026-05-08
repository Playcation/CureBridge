package com.example.contentservice.news.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.document.NewsDocument;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.entity.News;
import com.example.contentservice.news.repository.NewsRepository;
import com.example.contentservice.news.repository.NewsSearchRepository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

	private final NewsRepository newsRepository;
	private final NewsSearchRepository newsSearchRepository;

	// 최근 24시간 이내 게시물 가져와서 저장
	@Override
	public void saveRecentNews(List<NewsRequestDto> dtos) {
		DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime yesterday = now.minusHours(24);

    List<News> posts = dtos.stream()
        .filter(dto -> {
          LocalDateTime publishedAt = ZonedDateTime.parse(dto.getPubDate(), formatter)
              .toLocalDateTime();
          return publishedAt.isAfter(yesterday) && publishedAt.isBefore(now);
        })
        .filter(dto -> !newsRepository.existsByLink(dto.getLink()))
        .map(dto -> News.builder()
            .title(dto.getTitle())
            .link(dto.getLink())
            .publishedAt(ZonedDateTime.parse(dto.getPubDate(), formatter).toLocalDateTime())
            .build())
        .collect(Collectors.toList());
    newsRepository.saveAll(posts);

    // 💡 Elasticsearch에 저장
    List<NewsDocument> documents = posts.stream()
        .map(NewsDocument::fromEntity)
        .collect(Collectors.toList());

    newsSearchRepository.saveAll(documents);
  }

  // 게시물 다건 조회
  @Override
  public PagingDto<NewsResponseDto> getNewsAndPaging(Pageable pageable) {
    Page<News> newsPage = newsRepository.findAll(pageable);

    List<NewsResponseDto> newsDtoList = newsPage.getContent().stream()
        .map(NewsResponseDto::toDto)
        .toList();

    return new PagingDto<>(newsDtoList, newsPage.getTotalElements());
  }

	// 게시물 삭제
	@Override
	public void deleteNews(Long newsId) {
		newsRepository.findByIdOrElseThrow(newsId);
		newsRepository.deleteById(newsId);
		newsSearchRepository.deleteById(String.valueOf(newsId));
	}

  @Override
  @Transactional // MySQL 삭제 트랜잭션 보장
  public void cleanupOldNews(int days) {
    LocalDateTime threshold = LocalDateTime.now().minusDays(days);

		// 1. MySQL 데이터 삭제
		newsRepository.deleteByPublishedAtBefore(threshold);
	}

}