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
  private String title;
  private String content;
  private boolean isPrivate;
  private List<String> attachedFilePaths;
  private boolean isRelied;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static SupportResponseDto toDto(Support support, List<String> attachedFilePaths) {
    return SupportResponseDto.builder()
        .supportId(support.getId())
        .userId(support.getUserId())
        .title(support.getTitle())
        .content(support.getContent())
        .isPrivate(support.isPrivate())
        .attachedFilePaths(attachedFilePaths)
        .isRelied(support.isReplied())
        .createdAt(support.getCreatedAt())
        .updatedAt(support.getUpdatedAt())
        .build();
  }

}
