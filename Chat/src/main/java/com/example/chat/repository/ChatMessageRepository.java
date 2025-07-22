package com.example.chat.repository;

import com.example.chat.entity.Chat;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<Chat, String> {
  List<Chat> findByRoomIdOrderByTimestampAsc(String roomId);
}
