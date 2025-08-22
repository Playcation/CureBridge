package com.example.contentservice.notice.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {

  NoticeResponseDto createNotice(Long userId, NoticeRequestDto dto,
      List<MultipartFile> attachedFiles, List<MultipartFile> contentImages);

  NoticeResponseDto getNotice(Long noticeId);

  PagingDto<PagingNoticeResponseDto> getNoticesAndPaging(Pageable pageable);

  NoticeResponseDto updateNotice(Long noticeId, NoticeRequestDto dto);

  void deleteNotice(Long noticeId);
}