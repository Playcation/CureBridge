package com.example.chat.topic;

import com.example.chat.redis.sub.RedisSubscriber;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TopicManager {

  private final List<ChannelTopic> topics;

  public TopicManager() {
    this.topics = List.of(new ChannelTopic("chat")); // 단순한 하드코딩 or properties에서 읽기
  }

  public List<ChannelTopic> getAllTopics() {
    return topics;
  }
}
