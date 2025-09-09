package com.example.chat.controller;

import com.example.chat.dto.ChatMessageRequestDto;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class StompController {

   private final SimpMessageSendingOperations messageTemplate;
   private final ChatService chatMessageService;

  @MessageMapping("/{roomId}")
  public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequestDto requestDto) {
    System.out.println(requestDto.getMessage());
//    chatMessageService.saveMessage(roomId, requestDto);
    messageTemplate.convertAndSend("/topic/" + roomId, requestDto);
  }

}

