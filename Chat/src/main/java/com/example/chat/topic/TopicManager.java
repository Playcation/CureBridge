package com.example.chat.topic;

import com.example.chat.redise.sub.RedisSubscriber;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TopicManager {

  private final RedisMessageListenerContainer redisMessageListenerContainer;
  private final RedisSubscriber redisSubscriber;

  // 채팅방 ID -> 토픽 매핑
  private final Map<String, ChannelTopic> topics = new ConcurrentHashMap<>();

  /**
   * 채팅방 토픽 반환. 없으면 새로 만들고 구독
   */
  public ChannelTopic getTopic(String roomId) {
    return topics.computeIfAbsent(roomId, id -> {
      ChannelTopic topic = new ChannelTopic(id);
      redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
      return topic;
    });
  }
}
