package com.example.chat.service;

import com.example.chat.repository.ChatMessageRepository;
import com.example.chat.repository.ChatParticipantRepository;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.repository.ReadStatusRepository;
import com.example.memberservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;

  private final ChatParticipantRepository chatParticipantRepository;

  private final ChatMessageRepository chatMessageRepository;

  private final ReadStatusRepository readStatusRepository;
}
