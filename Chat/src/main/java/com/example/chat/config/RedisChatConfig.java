package com.example.chat.config;

import com.example.chat.service.RedisPubSubService;
import com.example.commonmodule.config.RedisTemplateConfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
@Configuration
public class RedisChatConfig {

  @Value("${REDIS_HOST}")
  private String host;

  @Value("${REDIS_PORT}")
  private int port;

  //  연결 기본 객체
//    추후에 커낵션 객체를 여러개 만들떄 @Qualifier에 이름 바꾸고 용도에 맞게 사용
  @Bean
  @Qualifier("chatPubSub")
  public RedisConnectionFactory chatPubSubFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(host);
    configuration.setPort(port);
//      레디스 pubs/pub에서는 특정 데이터베이스에 의존적이지 않음
//        TODO : db설정을 해주는건데 이건 일단 논의 후 설정
//        configuration.setDatabase(0);
    return new LettuceConnectionFactory(configuration);
  }

  //    publish객체
//    연결 기본객체와 마찬가지로 여러개 생성 가능
//    굳이 StringRedisTemplate따로 만든 이유는 채팅은 기본적으로 키벨류로 저장하려고 쓰는게 아님(메시지를 받고 뿌리기 때문에 String형식으로 뿌려버림)
  @Bean
  @Qualifier("chatPubSub")
  public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory) {
    return new StringRedisTemplate(redisConnectionFactory);
  }

  //    subscribe객체
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer
  (@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
      MessageListenerAdapter listenerAdapter) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory);
    container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
    return container;
  }

  //    redis에서 수신된 메시지를 처리하는 객체를 생성
  @Bean
  public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService) {
//      redisPubSubService의 특적 메서드가 수신된 메시지를 처리할수 있도록 지정
    return new MessageListenerAdapter(redisPubSubService, "onMessage");
  }
}
