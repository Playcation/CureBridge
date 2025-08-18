package com.example.chat;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.services.s3.AmazonS3;
import com.example.chat.dto.ChatMessageDto;
import com.example.chat.entity.Chat;
import com.example.chat.enums.MessageType;
import com.example.chat.redis.pub.RedisPublisher;
import com.example.chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource("classpath:application-test.properties")
@Import(TestConfig.class)
public class RedisSubscriberIntegrationTest {
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ChatMessageRepository chatMessageRepository;

  @Autowired
  private ObjectMapper objectMapper;

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

    String json = objectMapper.writeValueAsString(dto);

    // when
    redisTemplate.convertAndSend(json, "chatroom" + dto.getRoomId());

    // then (수신까지 기다림)
    Thread.sleep(500); // RedisSubscriber가 메시지 처리할 시간


    List<Chat> savedMessages = chatMessageRepository.findAll();
    assertEquals(1, savedMessages.size());

    Chat saved = savedMessages.get(0);
    assertEquals(dto.getSender(), saved.getSender());
    assertEquals(dto.getMessage(), saved.getMessage());
  }

}
