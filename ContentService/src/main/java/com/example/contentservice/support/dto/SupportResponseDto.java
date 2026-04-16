package com.example.contentservice.support.dto;

import com.example.contentservice.support.entity.Support;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupportResponseDto {

  private Long supportId;
  private Long userId;
  private String writerName;
  private String title;
  private String content;
  private boolean isPrivate;
  private List<String> attachedFilePaths;
  private boolean isReplied;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static SupportResponseDto toDto(Support support, String writerName,
      List<String> attachedFilePaths) {
    return SupportResponseDto.builder()
        .supportId(support.getId())
        .userId(support.getUserId())
        .writerName(writerName)
        .title(support.getTitle())
        .content(support.getContent())
        .isPrivate(support.isPrivate())
        .attachedFilePaths(attachedFilePaths)
        .isReplied(support.isReplied())
        .createdAt(support.getCreatedAt())
        .updatedAt(support.getUpdatedAt())
        .build();
  }

}
