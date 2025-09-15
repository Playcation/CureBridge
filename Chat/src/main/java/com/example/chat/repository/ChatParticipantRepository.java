package com.example.chat.repository;

import com.example.chat.domain.ChatParticipant;
import com.example.chat.domain.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
  List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

  Optional<ChatParticipant> findByChatRoomAndUserEmail(ChatRoom chatRoom, String senderEmail);
}
