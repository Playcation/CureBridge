package com.example.contentservice.news.dto;

import com.example.contentservice.news.document.NewsDocument;
import com.example.contentservice.news.entity.News;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsResponseDto {

  private Long id;
  private String title;
  private String link;
  private LocalDateTime publishedAt;

  public static NewsResponseDto toDto(News news) {
    return NewsResponseDto.builder()
        .id(news.getId())
        .title(news.getTitle())
        .link(news.getLink())
        .publishedAt(news.getPublishedAt())
        .build();
  }

  // 검색 결과 조회용
  public static NewsResponseDto fromDocument(NewsDocument doc, News news) {
    return NewsResponseDto.builder()
        .id(Long.valueOf(doc.getId()))
        .title(doc.getTitle())
        .link(news.getLink())
        .publishedAt(doc.getPublishedAt())
        .build();
  }
}