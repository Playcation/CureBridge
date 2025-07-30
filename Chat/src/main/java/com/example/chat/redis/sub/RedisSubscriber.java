package com.example.chat.redis.sub;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {

  private final ObjectMapper objectMapper;
  private final SimpMessageSendingOperations messagingTemplate;
  private final ChatMessageService chatMessageService;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      String body = new String(message.getBody());
      ChatMessageDto chatMessage = objectMapper.readValue(body, ChatMessageDto.class);

      // ✅ 메시지 저장
      chatMessageService.saveMessage(chatMessage);

      // ✅ WebSocket 구독자에게 전송
      messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);

    } catch (Exception e) {
      log.error("Redis 메시지 처리 중 오류", e);
    }
  }
}
