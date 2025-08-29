package com.example.chat.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ChatMessageDto {

  private String roomId;
  private String sender;
  private String message;



  public ChatMessageDto(String roomId, String sender, String message) {
    this.roomId = roomId;
    this.sender = sender;
    this.message = message;
  }
}
