package com.example.commonmodule.config;

import java.time.Duration;

import com.example.commonmodule.service.RedisPubSubService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class RedisTemplateConfig {

    @Value("${REDIS_HOST}")
    private String host;

    @Value("${REDIS_PORT}")
    private int port;

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(cf);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer()); // ObjectMapper 기반 변경도 가능
    return template;
  }

//  캐싱 설정
  @Bean
  public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10)) // 기본 TTL 10분
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()));

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultConfig)
        .build();
  }

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
      RedisMessageListenerContainer container = RedisMessageListenerContainer;
      container.setConnectionFactory(redisConnectionFactory);
      container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
    }

//    redis에서 수신된 메시지를 처리하는 객체를 생성
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService) {
//      redisPubSubService의 특적 메서드가 수신된 메시지를 처리할수 있도록 지정
        return new MessageListenerAdapter(redisPubSubService, "onMessage");
    }
}
