package com.example.contentservice.news.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.dto.TopKeywordResponseDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NewsSearchService {

  PagingDto<NewsResponseDto> searchByTitle(String keyword, Pageable pageable);

  List<TopKeywordResponseDto> aggregateTopKeywordsForDateRange(LocalDate gte, LocalDate lt,
      int size);
}
