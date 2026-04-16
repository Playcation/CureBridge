package com.example.contentservice.support.dto;

import com.example.contentservice.support.entity.Support;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupportDetailResponseDto {

  private Long id;
  private String title;
  private String content;
  private boolean isPrivate;
  private Long viewCount;
  private Long userId;
  private String writerName;
  private List<String> attachedFilePaths; // 첨부파일 리스트 추가

  // 답글 정보 (nullable)
  private boolean isReplied;
  private String replyContent;
  private LocalDateTime repliedAt;

  public static SupportDetailResponseDto toDto(
      Support support,
      String writerName,
      List<String> attachedFilePaths
  ) {
    return SupportDetailResponseDto.builder()
        .id(support.getId())
        .title(support.getTitle())
        .content(support.getContent())
        .isPrivate(support.isPrivate())
        .viewCount(support.getViewCount())
        .userId(support.getUserId())
        .writerName(writerName)
        .attachedFilePaths(attachedFilePaths)
        .isReplied(support.isReplied())
        .replyContent(support.isReplied() ? support.getReplyContent() : null)
        .repliedAt(support.isReplied() ? support.getRepliedAt() : null)
        .build();
  }
}