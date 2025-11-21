package com.example.chat.repository;

import com.example.chat.domain.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  List<ChatRoom> findByIsGroupChat(String isGroupChat);
}
