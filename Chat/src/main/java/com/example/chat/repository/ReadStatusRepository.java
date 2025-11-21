package com.example.chat.repository;

import com.example.chat.domain.ChatRoom;
import com.example.chat.domain.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {
    List<ReadStatus> findByChatRoomAndUserEmail(ChatRoom chatRoom, String userEmail);

    Long countByChatRoomAndUserEmailAndIsReadFalse(ChatRoom chatRoom, String userEmail);
}
