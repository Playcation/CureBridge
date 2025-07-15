package com.example.chat.topic;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class TopicManager {

  private final ConcurrentHashMap<String, ChannelTopic> topics = new ConcurrentHashMap<>();

  public ChannelTopic getTopic(String roomId) {
    return topics.computeIfAbsent(roomId, ChannelTopic::new);
  }

}
