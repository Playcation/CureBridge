package com.example.chat.redis.sub;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {

  private final ObjectMapper objectMapper;
  private final RedisTemplate redisTemplate;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      String onMessage = (String) redisTemplate.getStringSerializer()
          .deserialize(message.getBody());

      String data = objectMapper.readValue(onMessage, String.class);
      System.out.println(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
