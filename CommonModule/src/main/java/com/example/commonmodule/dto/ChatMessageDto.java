package com.example.commonmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

  private Long roomId;
  private String message;
//  TODO: 토큰에서 유저id를 찾으면 필요없을거 같음(삭제?)
  private String senderEmail;
}
