package com.example.chat.service;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.entity.Chat;
import com.example.chat.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;

  public void saveMessage(ChatMessageDto message) {
    Chat document = Chat.builder()
        .roomId(message.getRoomId())
        .sender(message.getSender())
        .message(message.getMessage())
        .type(message.getType())
        .timestamp(LocalDateTime.now())
        .build();

    chatMessageRepository.save(document);
  }
}
