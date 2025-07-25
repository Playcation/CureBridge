package com.example.contentservice.notice.service;

import java.util.List;

import com.example.contentservice.notice.dto.NoticeSearchResponseDto;

public interface NoticeSearchService {

	List<NoticeSearchResponseDto> searchByTitle(String keyword);

	List<NoticeSearchResponseDto> searchByAll(String keyword);
}

