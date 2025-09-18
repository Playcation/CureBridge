package com.example.chat.service;

import com.example.chat.config.UserClient;
import com.example.chat.domain.ChatMessage;
import com.example.chat.domain.ChatParticipant;
import com.example.chat.domain.ChatRoom;
import com.example.chat.domain.ReadStatus;
import com.example.chat.dto.ChatMessageDto;
import com.example.chat.dto.ChatRoomListResponseDto;
import com.example.chat.dto.MyChatListResDto;
import com.example.chat.repository.ChatMessageRepository;
import com.example.chat.repository.ChatParticipantRepository;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.repository.ReadStatusRepository;
import com.example.memberservice.dto.UserResponseDto;
import com.example.memberservice.entity.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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

  public void saveMessage(Long roomId, ChatMessageDto requestDto, String authorizationHeader) {
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

    if(chatRoom.getIsGroupChat().equals("N")) {
        throw new IllegalArgumentException("그룹채팅이 아닙니다");
    }

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

  public List<ChatMessageDto> getChatHistory(Long roomId, String authorizationHeader) {

      ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));

      Long userId = jwtUtil.findUserByToken(authorizationHeader);
      UserResponseDto userDto = userCacheService.getUserByCache(authorizationHeader, userId);

      List<ChatParticipant> chatParticipantList = chatParticipantRepository.findByChatRoom(chatRoom);

      boolean check = false;

      for(ChatParticipant chatParticipant : chatParticipantList) {
          if (chatParticipant.getUserEmail().equals(userDto.getEmail())) {
              check =  true;
          }
      }

      if(!check) {
          new IllegalArgumentException("본인이 속하지 않은 채팅방입니다");
      }

      List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);

      List<ChatMessageDto> chatMessageDtos = new ArrayList<>();

      for(ChatMessage chatMessage : chatMessages) {
          ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                  .message(chatMessage.getContent())
                  .senderEmail(userDto.getEmail())
                  .build();
          chatMessageDtos.add(chatMessageDto);
      }
    return  chatMessageDtos;
  }

  public boolean isRoomParticipant(String email, Long roomId, String bearerToken) {
      ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));

//      TODO: 일단 stompHandle에서 bearerToken를 가져오긴 했는데 맞는지 확인해야함
      Long userId = jwtUtil.findUserByToken(bearerToken);
      UserResponseDto userDto = userCacheService.getUserByCache(bearerToken, userId);

      List<ChatParticipant> chatParticipantList = chatParticipantRepository.findByChatRoom(chatRoom);
      for(ChatParticipant chatParticipant : chatParticipantList) {
          if(chatParticipant.getUserEmail().equals(userDto.getEmail)) {
              return true;
          }
      }
      return false;
  }

//  사용자가 속해있는 채팅방의 메시지 읽음으로 변경(메세지 읽음처리 기능은 프론트에서 언제 사용할지 정해야함)
  public void messageRead(Long roomId, String authorizationHeader) {
      ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));

      Long userId = jwtUtil.findUserByToken(authorizationHeader);
      UserResponseDto userDto = userCacheService.getUserByCache(authorizationHeader, userId);
      List<ReadStatus> readStatusList = readStatusRepository.findByChatRoomAndUserEmail(chatRoom, userDto.getEmail);

      for(ReadStatus readStatus : readStatusList) {
          readStatus.updateReadStatus(true);
      }

  }

    public List<MyChatListResDto> getMyChatRoom(String authorizationHeader) {
      Long userId = jwtUtil.findUserByToken(authorizationHeader);
      UserResponseDto userDto = userCacheService.getUserByCache(authorizationHeader, userId);

      List<ChatParticipant> chatParticipantList = chatParticipantRepository.findAllByUserEmail(userDto.getEmail);
      List<MyChatListResDto> myChatListResDtos = new ArrayList<>();
      for(ChatParticipant chatParticipant : chatParticipantList) {
          Long count = readStatusRepository.countByChatRoomAndUserEmailAndIsReadFalse(chatParticipant.getChatRoom(), userDto.getEmail);
          MyChatListResDto dto = MyChatListResDto.builder()
                  .roomId(chatParticipant.getChatRoom().getId())
                  .roomName(chatParticipant.getChatRoom().getName())
                  .isGroupChat(chatParticipant.getChatRoom().getIsGroupChat())
                  .unRaedCount(count)
                  .build();
          myChatListResDtos.add(dto);
      }
      return myChatListResDtos;
  }

    public void leaveGroupRoom(Long roomId, String authorizationHeader) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));

        Long userId = jwtUtil.findUserByToken(authorizationHeader);
        UserResponseDto userDto = userCacheService.getUserByCache(authorizationHeader, userId);

        if(!chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("단체 채팅방이 아닙니다");
        }
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndUserEmail(chatRoom, userDto.getEmail).orElseThrow(()->new EntityNotFoundException("참여자를 찾을 수 없습니다"));
        chatParticipantRepository.delete(chatParticipant);

        List<ChatParticipant> chatParticipantList = chatParticipantRepository.findByChatRoom(chatRoom);
        if(!chatParticipantList.isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    public Long getOrCreatePrivateRoom(Long orderUserId, String authorizationHeader) {

        Long userId = jwtUtil.findUserByToken(authorizationHeader);
        UserResponseDto userDto = userCacheService.getUserByCache(authorizationHeader, userId);

//        TODO : 다른 유저를 찾는 컨트롤러 필요
//        UserResponseDto orderUserDto = userClient.findUser()

//        나와 상대방이 1:1 채팅방에 이미 참여하고 있으면 해당 roomId return
        Optional<ChatRoom> chatroom = chatParticipantRepository.findExistingPrivateRoom(userDto.getEmail(), orderUserDto.getEmail);
    if(chatroom.isPresent()) {
        return chatroom.get().getId();
    }

//    만약 1:1 채팅방이 없을경우 기존 채팅방 개설
        ChatRoom newRoom = ChatRoom.builder()
                .isGroupChat("N")
                .name(userDto.getName() + "님과" + orderUserDto.getName() + "님의 채팅방")
                .build();
    chatRoomRepository.save(newRoom);

//    두사람 모두 참여자로 새롭게 추가
        addParticipantToRoom(newRoom, userDto.getEmail());
        addParticipantToRoom(newRoom, orderUserDto.getEmail());

        return newRoom.getId();
    }

}
