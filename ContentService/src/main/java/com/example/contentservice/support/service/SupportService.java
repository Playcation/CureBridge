package com.example.contentservice.support.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.support.dto.PagingSupportResponseDto;
import com.example.contentservice.support.dto.SupportDetailResponseDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface SupportService {

  SupportResponseDto createSupport(SupportRequestDto dto, Long userId,
      List<MultipartFile> attachedFiles);

  SupportDetailResponseDto getSupport(Long supportId, Long userId, String role);

  PagingDto<PagingSupportResponseDto> getSupportsAndPaging(Pageable pageable);

  SupportResponseDto updateSupport(Long supportId, Long userId, SupportRequestDto dto);

  void deleteSupport(Long supportId, Long userId);
}