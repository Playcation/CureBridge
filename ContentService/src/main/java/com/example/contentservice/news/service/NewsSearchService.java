package com.example.contentservice.news.service;

import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NewsSearchService {

  PagingDto<NewsResponseDto> searchByTitle(String keyword, Pageable pageable);

  List<StringTermsBucket> aggregateTopKeywordsForDateRange(LocalDate gte, LocalDate lt, int size)
      throws IOException;
}
