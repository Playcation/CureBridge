package com.example.contentservice.support.dto;

import com.example.contentservice.support.document.SupportDocument;
import com.example.contentservice.support.entity.Support;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PagingSupportResponseDto {

  private Long supportId;
  private Long userId;
  private String writerName;
  private String title;
  private String content;
  private boolean isPrivate;
  private boolean isReplied;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static PagingSupportResponseDto toDto(Support support, String writerName) {
    return PagingSupportResponseDto.builder()
        .supportId(support.getId())
        .userId(support.getUserId())
        .writerName(writerName)
        .title(support.getTitle())
        .isPrivate(support.isPrivate())
        .isReplied(support.isReplied())
        .createdAt(support.getCreatedAt())
        .updatedAt(support.getUpdatedAt())
        .build();
  }

  public static PagingSupportResponseDto fromDocument(SupportDocument doc, Support support) {
    return PagingSupportResponseDto.builder()
        .supportId(Long.valueOf(doc.getId()))
        .userId(support.getUserId())
        .title(doc.getTitle())
        .content(doc.getContent())
        .isPrivate(support.isPrivate())
        .isReplied(support.isReplied())
        .createdAt(support.getCreatedAt())
        .updatedAt(support.getUpdatedAt())
        .build();
  }
}
