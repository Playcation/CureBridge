package com.example.contentservice.notice.dto;

import com.example.contentservice.notice.entity.Notice;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NoticeResponseDto {

  // 공지사항 고유 ID
  private Long noticeId;

  // 작성자 ID
  private Long userId;

  // 작성자 이름
  private String writerName;

  // 공지사항 제목
  private String title;

  // 공지사항 본문 내용
  private String content;

  // 조회수
  private Long viewCount;

  // 생성일시
  private LocalDateTime createdAt;

  // 수정일시
  private LocalDateTime updatedAt;

  // 본문 내 이미지 경로 목록
  private List<String> contentImagePaths;

  // 첨부 파일 경로 목록
  private List<String> attachedFilePaths;

  /**
   * Notice 엔티티를 기반으로 Response DTO로 변환하는 정적 팩토리 메서드 contentImagePaths, attachedFilePaths는 별도의 파일 저장
   * 로직에서 전달받는다.
   */
  public static NoticeResponseDto toDto(
      Notice notice,
      String writerName,
      List<String> contentImagePaths,
      List<String> attachedFilePaths
  ) {
    return NoticeResponseDto.builder()
        .noticeId(notice.getId())
        .userId(notice.getUserId())
        .writerName(writerName)
        .title(notice.getTitle())
        .content(notice.getContent())
        .viewCount(notice.getViewCount())
        .createdAt(notice.getCreatedAt())
        .updatedAt(notice.getUpdatedAt())
        .contentImagePaths(contentImagePaths)
        .attachedFilePaths(attachedFilePaths)
        .build();
  }
}