package com.example.contentservice.support.dto;

import com.example.contentservice.support.entity.Support;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupportDetailResponseDto {

  private Long id;
  private String title;
  private String content;
  private Long userId;

  // 답글 정보 (nullable)
  private Boolean isReplied;
  private String replyContent;
  private LocalDateTime repliedAt;

  public static SupportDetailResponseDto toDto(Support support) {
    return SupportDetailResponseDto.builder()
        .id(support.getId())
        .title(support.getTitle())
        .content(support.getContent())
        .userId(support.getUserId())
        .isReplied(support.isReplied())
        .replyContent(support.isReplied() ? support.getReplyContent() : null)
        .repliedAt(support.isReplied() ? support.getRepliedAt() : null)
        .build();
  }
}