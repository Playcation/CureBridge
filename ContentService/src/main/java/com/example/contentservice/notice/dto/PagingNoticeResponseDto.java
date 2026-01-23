package com.example.contentservice.notice.dto;

import com.example.contentservice.notice.document.NoticeDocument;
import com.example.contentservice.notice.entity.Notice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 공지사항 목록 조회 및 검색 결과 응답에 사용되는 DTO. Notice 엔티티 또는 Elasticsearch Document에서 데이터를 받아 생성된다.
 */
@Builder
@Getter
public class PagingNoticeResponseDto {

  // 공지사항 ID
  private Long noticeId;

  // 작성자 ID
  private Long userId;

  // 공지사항 제목
  private String title;

  // 공지사항 내용 (검색 결과에서만 포함)
  private String content;

  // 조회수
  private Long viewCount;

  // 생성 시각
  private LocalDateTime createdAt;

  // 수정 시각
  private LocalDateTime updatedAt;

  /**
   * 일반 목록 조회용 DTO 변환 메서드. 엔티티에서 필요한 기본 정보만 가져온다.
   */
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

  /**
   * 검색 결과 조회용 DTO 변환 메서드. Elasticsearch 문서의 값을 우선 사용하며, 필요 시 Notice 엔티티에서 부가 정보를 가져온다.
   */
  public static PagingNoticeResponseDto fromDocument(NoticeDocument doc, Notice notice) {
    return PagingNoticeResponseDto.builder()
        .noticeId(Long.valueOf(doc.getId()))   // ES 문서 ID는 문자열이므로 Long 변환
        .userId(notice.getUserId())           // 작성자 정보는 엔티티에서 가져옴
        .title(doc.getTitle())
        .content(doc.getContent())
        .viewCount(notice.getViewCount())
        .createdAt(doc.getCreatedAt())
        .updatedAt(notice.getUpdatedAt())
        .build();
  }
}