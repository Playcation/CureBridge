package com.example.contentservice.notice.dto;

import com.example.contentservice.notice.document.NoticeDocument;
import com.example.contentservice.notice.entity.Notice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 조회용 dto
 */
@Builder
@Getter
public class PagingNoticeResponseDto {

  private Long noticeId;
  private Long userId;
  private String title;
  private String content;
  private Long viewCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 단순 다건 조회용
  public static PagingNoticeResponseDto toDto(Notice notice) {
    return PagingNoticeResponseDto.builder()
        .noticeId(notice.getId())
        .userId(notice.getUserId())
        .title(notice.getTitle())
        .viewCount(notice.getViewCount())
        .createdAt(notice.getCreatedAt())
        .updatedAt(notice.getUpdatedAt())
        .build();
  }

  // 검색 결과 조회용
  public static PagingNoticeResponseDto fromDocument(NoticeDocument doc, Notice notice) {
    return PagingNoticeResponseDto.builder()
        .noticeId(Long.valueOf(doc.getId()))
        .userId(notice.getUserId())
        .title(doc.getTitle())
        .content(doc.getContent())
        .viewCount(notice.getViewCount())
        .createdAt(doc.getCreatedAt())
        .updatedAt(notice.getUpdatedAt())
        .build();
  }
}
