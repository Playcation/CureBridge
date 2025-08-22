package com.example.contentservice.notice.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import org.springframework.data.domain.Pageable;

public interface NoticeSearchService {

  PagingDto<PagingNoticeResponseDto> searchByTitle(String keyword, Pageable pageable);

  PagingDto<PagingNoticeResponseDto> searchByAll(String keyword, Pageable pageable);
}

