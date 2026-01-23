package com.example.contentservice.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {

  // 공지사항 제목
  private String title;

  // 공지사항 본문 내용
  private String content;

  // 공지사항 생성 또는 수정 시 사용되는 요청 DTO
  public NoticeRequestDto(String title, String content) {
    this.title = title;
    this.content = content;
  }
}