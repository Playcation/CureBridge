package com.example.contentservice.news.service;

import java.util.List;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;

public interface NewsService {

	void saveRecentNews(List<NewsRequestDto> dtos);

	PagingDto<NewsResponseDto> getNewsAndPaging(int page);

	void deleteNews(Long newsId);
}