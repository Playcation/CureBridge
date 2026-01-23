package com.example.contentservice.notice.dto;

import com.example.contentservice.notice.document.NoticeDocument;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeSearchResponseDto {

  // 공지사항 ID (Elasticsearch 문서의 ID 기반)
  private Long id;

  // 공지사항 제목
  private String title;

  // 공지사항 내용
  private String content;

  // 공지사항 생성 시각
  private LocalDateTime createdAt;

  /**
   * Elasticsearch에서 조회한 NoticeDocument를 검색 응답 DTO로 변환하는 정적 팩토리 메서드. 검색 기능에서 사용된다.
   */
  public static NoticeSearchResponseDto fromDocument(NoticeDocument doc) {
    return NoticeSearchResponseDto.builder()
        .id(Long.valueOf(doc.getId()))     // ES 문서 ID는 문자열이므로 Long으로 변환
        .title(doc.getTitle())
        .content(doc.getContent())
        .createdAt(doc.getCreatedAt())
        .build();
  }
}