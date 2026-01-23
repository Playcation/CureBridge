package com.example.contentservice.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopKeywordResponseDto {

  private String keyword;
  private long count;
}