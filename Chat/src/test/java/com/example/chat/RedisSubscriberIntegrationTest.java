package com.example.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.entity.Chat;
import com.example.chat.enums.MessageType;
import com.example.chat.redis.pub.RedisPublisher;
import com.example.chat.repository.ChatMessageRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisSubscriberIntegrationTest {
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ChatMessageRepository chatMessageRepository;

  @Autowired
  private RedisPublisher redisPublisher;

  @BeforeEach
  void setUp() {
    chatMessageRepository.deleteAll();
    redisTemplate.getConnectionFactory().getConnection().flushDb();
  }

  @Test
  void 메시지_발행_후_구독자_저장_확인() throws Exception {
    // given
    ChatMessageDto dto = ChatMessageDto.builder()
        .roomId("test-room")
        .sender("redis-test")
        .message("Hello Redis!")
        .type(MessageType.TALK)
        .build();

    // when
    redisPublisher.publish(dto);

    // then (수신까지 기다림)
    Thread.sleep(500); // RedisSubscriber가 메시지 처리할 시간

    List<Chat> saved = chatMessageRepository.findAll();
    assertEquals(1, saved.size());
    assertEquals("Hello Redis!", saved.get(0).getMessage());
  }

}
