package com.example.chat.controller;

import com.example.chat.dto.ChatMessageRequestDto;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class StompController {

   private final SimpMessageSendingOperations messageTemplate;
   private final ChatService chatService;

  @MessageMapping("/{roomId}")
  public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequestDto requestDto,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    System.out.println(requestDto.getMessage());
    chatService.saveMessage(roomId, requestDto, authorizationHeader);
    messageTemplate.convertAndSend("/topic/" + roomId, requestDto);
  }

}

