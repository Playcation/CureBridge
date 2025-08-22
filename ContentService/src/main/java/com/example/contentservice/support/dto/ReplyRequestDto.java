package com.example.contentservice.support.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@NoArgsConstructor
public class ReplyRequestDto {

  @NotNull
  private Long supportId; // 문의 ID

  private String replyContent; // 답글 내용
}