package com.example.chat.dto;


import com.example.chat.enums.MessageType;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ChatMessageDto {

  private String roomId;
  private String sender;
  private String message;
  private MessageType type;


  public ChatMessageDto(String roomId, String sender, String message, MessageType type) {
    this.roomId = roomId;
    this.sender = sender;
    this.message = message;
    this.type = type;
  }
}
