package com.example.contentservice.notice.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;

public interface NoticeService {
	NoticeResponseDto createNotice(NoticeRequestDto dto, Long userId);

	NoticeResponseDto getNotice(Long noticeId);

	PagingDto<NoticeResponseDto> getNoticesAndPaging(int page);

	NoticeResponseDto updateNotice(Long noticeId, NoticeRequestDto dto);

	void deleteNotice(Long noticeId);
}