package com.example.contentservice.news.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import org.springframework.data.domain.Pageable;

public interface NewsSearchService {

  PagingDto<NewsResponseDto> searchByTitle(String keyword, Pageable pageable);

}
