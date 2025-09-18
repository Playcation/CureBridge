package com.example.chat.repository;

import com.example.chat.domain.ChatParticipant;
import com.example.chat.domain.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
  List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

  Optional<ChatParticipant> findByChatRoomAndUserEmail(ChatRoom chatRoom, String senderEmail);

  List<ChatParticipant> findAllByUserEmail(String userEmail);

  @Query("SELECT cp.chatRoom FROM ChatParticipant cp " +
          "WHERE cp.chatRoom.isGroupChat = 'N' " +
          "AND cp.userEmail IN (:myEmail, :otherUserEmail) " +
          "GROUP BY cp.chatRoom HAVING COUNT(cp.chatRoom) = 2")
//          일단 제미니가 위에거 쓰는게 좋다고 만들어줬는데 여차하면 아래에 주석처리된거 쓰겠습니다
//  @Query("SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 ON cp1.chatRoom.id = cp2.chatRoom.id WHERE cp1.userEmail = :myEmail AND cp2.userEmail = :orderUserEmail AND cp1.chatRoom.isGroupChat = 'N'")
  Optional<ChatRoom> findExistingPrivateRoom(@Param("myEmail") String myEmail, @Param("otherUserEmail") String otherUserEmail);
}
