package com.example.contentservice.notice.service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface OrgNoticeService {

  NoticeResponseDto createOrgNotice(Long orgId, Long userId, NoticeRequestDto dto,
      List<MultipartFile> attachedFiles, List<MultipartFile> contentImages);

  NoticeResponseDto getOrgNotice(Long orgId, Long noticeId);

  PagingDto<PagingNoticeResponseDto> getOrgNoticesAndPaging(Long orgId, Pageable pageable);

  NoticeResponseDto updateOrgNotice(Long orgId, Long noticeId, NoticeRequestDto dto);

  void deleteOrgNotice(Long orgId, Long noticeId);
}