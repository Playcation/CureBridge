package com.example.chat.chatcontroller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.enums.MessageType;
import com.example.chat.redis.pub.RedisPublisher;
import com.example.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ChatController {

  private final RedisPublisher redisPublisher;
  private final ChatMessageService chatMessageService;

  @MessageMapping("/chat/message")
  public void sendChatMessage(ChatMessageDto message) {

    // 입장 메시지 텍스트 처리
    if (message.getType() == MessageType.ENTER) {
      message = ChatMessageDto.builder()
          .roomId(message.getRoomId())
          .sender(message.getSender())
          .type(MessageType.ENTER)
          .message(message.getSender() + "님이 입장하셨습니다.")
          .build();
    }

    // ✅ 메시지를 Redis에 발행
    redisPublisher.publish(message);
  }
}

