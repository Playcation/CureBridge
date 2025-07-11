package com.example.chat.redise.config;

import com.example.chat.redise.sub.RedisSubscriber;
import com.example.chat.topic.TopicManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  private final RedisSubscriber redisSubscriber;
  private final TopicManager topicManager;

  @Bean
  public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);

    // 모든 채팅방의 메시지를 수신
    container.addMessageListener(redisSubscriber, new PatternTopic("chatroom.*"));

    return container;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(cf);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer()); // ObjectMapper 기반 변경도 가능
    return template;
  }
}

