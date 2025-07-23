package com.example.contentservice.support.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;

public interface SupportService {
	SupportResponseDto createSupport(SupportRequestDto dto, Long userId);

	SupportResponseDto getSupport(Long supportId);

	PagingDto<SupportResponseDto> getBoardsAndPaging(int page);

	SupportResponseDto updateSupport(Long supportId, SupportRequestDto dto);

	void deleteSupport(Long supportId);
}