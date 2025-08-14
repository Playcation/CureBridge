package com.example.contentservice.support.dto;

import com.example.contentservice.support.entity.Support;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyResponseDto {

  private Long supportId;
  private String replyContent;
  private LocalDateTime repliedAt;
  private boolean isReplied;

  public static ReplyResponseDto toDto(Support support) {
    return ReplyResponseDto.builder()
        .supportId(support.getId())
        .replyContent(support.getReplyContent())
        .repliedAt(support.getRepliedAt())
        .isReplied(support.isReplied())
        .build();
  }
}