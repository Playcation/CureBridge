package com.example.commonmodule.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class RedisTemplateConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(cf);

    // 🔹 key는 문자열
    template.setKeySerializer(new StringRedisSerializer());

    // 🔹 ObjectMapper 커스터마이징
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule()); // ✅ LocalDateTime 지원
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 🔹 value를 JSON으로 직렬화
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

    // optional — hash 구조 사용할 때도 동일한 serializer 적용
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

    template.afterPropertiesSet();
    return template;
  }


  @Bean
  @Primary
  public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    objectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );

    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);

    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer)
        );

		Map<String, RedisCacheConfiguration> totalConfigs = new HashMap<>();

		totalConfigs.put("news_top_keywords", config.entryTtl(Duration.ofHours(25)));
		totalConfigs.put("news_keyword_results", config.entryTtl(Duration.ofHours(25)));

		totalConfigs.put("calendar_monthly", config.entryTtl(Duration.ofDays(7))); // 월간은 7일
		totalConfigs.put("calendar_daily", config.entryTtl(Duration.ofHours(24)));

		return RedisCacheManager.builder(cf)
			.cacheDefaults(config)
			.withInitialCacheConfigurations(totalConfigs)
			.build();
	}


}
