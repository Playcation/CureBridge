package com.example.chat.repository;

import com.example.chat.entity.Chat;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableMongoRepositories(basePackages = "com.example.chat.repository.mongo")
public interface ChatMessageRepository extends MongoRepository<Chat, String> {
  List<Chat> findByRoomIdOrderByTimestampAsc(String roomId);
}
