package com.example.contentservice.support.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.support.dto.PagingSupportResponseDto;
import org.springframework.data.domain.Pageable;

public interface SupportSearchService {

  PagingDto<PagingSupportResponseDto> searchByTitle(String keyword, Pageable pageable);

  PagingDto<PagingSupportResponseDto> searchByAll(String keyword, Pageable pageable);
}
