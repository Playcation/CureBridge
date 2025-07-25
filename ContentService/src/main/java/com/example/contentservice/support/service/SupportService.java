package com.example.contentservice.support.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;

public interface SupportService {
	SupportResponseDto createSupport(SupportRequestDto dto, Long userId);

	SupportResponseDto getSupport(Long supportId);

	PagingDto<SupportResponseDto> getSupportsAndPaging(int page);

	void deleteSupport(Long supportId);
}