package com.example.contentservice.news.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NewsService {

  void saveRecentNews(List<NewsRequestDto> dtos);

  PagingDto<NewsResponseDto> getNewsAndPaging(Pageable pageable);

  void deleteNews(Long newsId);

  void cleanupOldNews(int days);
}