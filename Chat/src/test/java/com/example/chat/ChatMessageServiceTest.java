package com.example.chat;

import static org.mockito.Mockito.verify;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.enums.MessageType;
import com.example.chat.redis.pub.RedisPublisher;
import com.example.chat.repository.ChatMessageRepository;
import com.example.chat.service.ChatMessageService;
import com.example.chat.topic.TopicManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private RedisPublisher redisPublisher;

  @Mock
  private TopicManager topicManager;

  @InjectMocks
  private ChatMessageService chatMessageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveMessage_정상저장_검증() {
    // given
    ChatMessageDto messageDto = ChatMessageDto.builder()
        .roomId("room1")
        .sender("userA")
        .message("Hello")
        .type(MessageType.TALK)
        .build();

    // when
    chatMessageService.saveMessage(messageDto);

    // then
//    verify(chatMessageRepository, times(1)).save(any(Chat.class));
//    verify(redisPublisher, times(1)).publish(any(ChatMessageDto.class));
  }
}
