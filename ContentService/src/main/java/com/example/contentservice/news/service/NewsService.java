package com.example.contentservice.news.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.entity.News;
import com.example.contentservice.news.repository.NewsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {

	private final NewsRepository newsRepository;

	public void saveRecentNews(List<NewsRequestDto> dtos) {
		DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime yesterday = now.minusHours(24);

		List<News> posts = dtos.stream()
			.filter(dto -> {
				LocalDateTime publishedAt = ZonedDateTime.parse(dto.getPubDate(), formatter).toLocalDateTime();
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
	}

	public PagingDto<NewsResponseDto> getNewsAndPaging(int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));
		Page<News> newsPage = newsRepository.findAll(pageable);

		List<NewsResponseDto> newsDtoList = newsPage.getContent().stream()
			.map(NewsResponseDto::toDto)
			.toList();

		return new PagingDto<>(newsDtoList, newsPage.getTotalElements());
	}

	public void deleteNews(Long newsId) {
		newsRepository.findByIdOrElseThrow(newsId);

		newsRepository.deleteById(newsId);
	}
}