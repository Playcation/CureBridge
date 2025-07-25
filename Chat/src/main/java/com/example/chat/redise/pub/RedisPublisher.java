package com.example.chat.redise.pub;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.topic.TopicManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

  private final RedisTemplate<String, Object> redisTemplate;
  private final TopicManager topicManager;
  private final ObjectMapper objectMapper;

  public void publish(ChatMessageDto message) {
    ChannelTopic topic = topicManager.getTopic(message.getRoomId());
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }
}
