package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.service.ChatService;
import com.example.chat.service.RedisPubSubService;
import com.example.commonmodule.config.TokenSettings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class StompController {

   private final SimpMessageSendingOperations messageTemplate;
   private final ChatService chatService;
   private final RedisPubSubService redisPubSubService;

  @MessageMapping("/{roomId}")
  public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto requestDto,
      StompHeaderAccessor accessor)
      throws JsonProcessingException {
    Long userId = (Long) accessor.getSessionAttributes().get("userId");
    chatService.saveMessage(roomId, requestDto, userId);
    requestDto.chatMessageDto(roomId, requestDto.getMessage(), requestDto.getSenderEmail());
    ObjectMapper objectMapper = new ObjectMapper();
    String message = objectMapper.writeValueAsString(requestDto);
    redisPubSubService.publish("chat", message);
  }

}

