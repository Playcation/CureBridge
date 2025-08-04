package com.example.contentservice.support.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SupportRequestDto {

  private String title;
  private String content;
  private boolean isReplied;
  private boolean isPrivate;

  public SupportRequestDto(String title, String content, boolean isReplied, boolean isPrivate) {
    this.title = title;
    this.content = content;
    this.isPrivate = isPrivate;
    this.isReplied = isReplied;
  }
}