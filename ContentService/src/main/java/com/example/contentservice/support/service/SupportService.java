package com.example.contentservice.support.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.support.dto.SupportDetailResponseDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface SupportService {

  SupportResponseDto createSupport(SupportRequestDto dto, Long userId,
      List<MultipartFile> attachedFiles);

  SupportDetailResponseDto getSupport(Long supportId);

  PagingDto<SupportResponseDto> getSupportsAndPaging(int page);

  SupportResponseDto updateSupport(Long supportId, SupportRequestDto dto);

  void deleteSupport(Long supportId);
}