package com.example.contentservice.support.dto;


import com.example.contentservice.support.document.SupportDocument;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupportSearchResponseDto {

  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;

  public static com.example.contentservice.support.dto.SupportSearchResponseDto fromDocument(
      SupportDocument doc) {
    return com.example.contentservice.support.dto.SupportSearchResponseDto.builder()
        .id(Long.valueOf(doc.getId()))
        .title(doc.getTitle())
        .content(doc.getContent())
        .createdAt(doc.getCreatedAt())
        .build();
  }
}