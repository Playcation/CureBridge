package com.example.chat.service;

import com.example.chat.config.UserClient;
import com.example.chat.domain.ChatMessage;
import com.example.chat.domain.ChatParticipant;
import com.example.chat.domain.ChatRoom;
import com.example.chat.domain.ReadStatus;
import com.example.chat.dto.ChatMessageRequestDto;
import com.example.chat.dto.ChatRoomListResponseDto;
import com.example.chat.repository.ChatMessageRepository;
import com.example.chat.repository.ChatParticipantRepository;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.repository.ReadStatusRepository;
import com.example.memberservice.dto.UserResponseDto;
import com.example.memberservice.entity.User;
import jakarta.persistence.Cacheable;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field.Str;
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

  private final UserCacheService userCacheService;

  private final UserClient userClient;

  public void saveMessage(Long roomId, ChatMessageRequestDto requestDto, String authorizationHeader) {
//    채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
//    보낸 사람 조회
//    TODO : findUserByToken 위치가 옮겨질때 까지 대기(jwtUtil 내부에서도 많이 쓰는 매서드라 옯기기 어려움)
    Long senderId = jwtUtil.findUserByToken(authorizationHeader);

    // ✅ 캐시 적용된 유저 조회
    UserResponseDto sender = userCacheService.getUserByCache(authorizationHeader, senderId);

//    메시지 저장
    ChatMessage chatMessage = ChatMessage.builder()
        .chatRoom(chatRoom)
        .userEmail(sender.getEmail())
        .content(requestDto.getMessage())
        .build();
    chatMessageRepository.save(chatMessage);

//    사용자별 읽음여부 저장
    List<ChatParticipant> chatParticipantList = chatParticipantRepository.findByChatRoom(chatRoom);
    for(ChatParticipant c : chatParticipantList) {
      ReadStatus readStatus = ReadStatus.builder()
          .chatRoom(chatRoom)
          .userEmail(c.getUserEmail())
          .chatMessage(chatMessage)
          .isRead(c.getUserEmail().equals(sender.getEmail()))
          .build();
      readStatusRepository.save(readStatus);
    }
  }

  public void createGroupRoom(String roomName, String authorizationHeader) {

    Long senderId = jwtUtil.findUserByToken(authorizationHeader);
//    TODO : 서비스에서 사용하려면 DTO가 있어야하는데 나중에 DTO를 commonmodule에 모아놓는게 좋아보임 지금 당장은 import하여 사용(나중에 고침)
    UserResponseDto sender = userCacheService.getUserByCache(authorizationHeader, senderId);

//    채팅방 생성
    ChatRoom chatRoom = ChatRoom.builder()
        .name(roomName)
        .isGroupChat("Y")
        .build();
    chatRoomRepository.save(chatRoom);

//    채팅참여자로 개설자를 추가
    ChatParticipant chatParticipant = ChatParticipant.builder()
        .chatRoom(chatRoom)
        .userEmail(sender.getEmail())
        .build();
    chatParticipantRepository.save(chatParticipant);
  }

  public List<ChatRoomListResponseDto> getGroupchatRooms() {
    List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
    List<ChatRoomListResponseDto> dtos = new ArrayList<>();
    for (ChatRoom chatRoom : chatRooms) {
      ChatRoomListResponseDto dto = ChatRoomListResponseDto
          .builder()
          .roomId(chatRoom.getId())
          .roomName(chatRoom.getName())
          .build();
      dtos.add(dto);
    }
    return dtos;
  }

  public void addParticipantToGroupChat(Long roomId, String authorizationHeader) {
//    채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));

//    user조회
    Long senderId = jwtUtil.findUserByToken(authorizationHeader);

    UserResponseDto sender = userCacheService.getUserByCache(authorizationHeader, senderId);

//    이미 참여자인지 검증
    Optional<ChatParticipant> participant =  chatParticipantRepository.findByChatRoomAndUserEmail(chatRoom, sender.getEmail());
    if(!participant.isPresent()) {
      addParticipantToRoom(chatRoom, sender.getEmail());
    }
  }

  //    ChatParticipant 객체 생성 후 저장
  public void addParticipantToRoom(ChatRoom chatRoom, String userEmail) {
    ChatParticipant chatParticipant = ChatParticipant.builder()
        .chatRoom(chatRoom)
        .userEmail(userEmail)
        .build();
    chatParticipantRepository.save(chatParticipant);
  }

}
