package com.example.chat.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class StompController {

   private final SimpMessageSendingOperations messageTemplate;

  @MessageMapping("/{roomId}")
  public String sendMessage(@DestinationVariable Long roomId, String message) {
    System.out.println(message);
    messageTemplate.convertAndSend("/topic/" + roomId, message);
    return message;
  }

}

