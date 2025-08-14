package com.example.contentservice.notice.dto;

import com.example.contentservice.notice.entity.Notice;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NoticeResponseDto {

  private Long noticeId;
  private Long userId;
  private String title;
  private String content;
  private Long viewCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  //첨부 파일, 글 중간 사진 컬럼
  private List<String> contentImagePaths;
  private List<String> attachedFilePaths;


  public static NoticeResponseDto toDto(Notice notice, List<String> contentImagePaths,
      List<String> attachedFilePaths) {
    return NoticeResponseDto.builder()
        .noticeId(notice.getId())
        .userId(notice.getUserId())
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
