package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.dto.ChatRoomListResponseDto;
import com.example.chat.dto.MyChatListResDto;
import com.example.chat.service.ChatService;
import com.example.memberservice.security.TokenSettings;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@AllArgsConstructor
public class ChatController {

  private final ChatService chatService;

//  그룹채팅방 개설
  @PostMapping("/room/group/cerate")
  public ResponseEntity<?> createGroupRoom(@RequestParam String roomName,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    chatService.createGroupRoom(roomName, authorizationHeader);
    return ResponseEntity.ok().build();
  }

//  그룹채팅 목록조회
  @GetMapping("/room/group/list")
  public ResponseEntity<?> getGroupChatRooms() {
    List<ChatRoomListResponseDto> chatRooms =  chatService.getGroupchatRooms();
    return new ResponseEntity<>(chatRooms, HttpStatus.OK);
  }

//  그룹채팅 참여(초대x)
  @PostMapping("/room/group/{roomId}/join")
  public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId,
  @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    chatService.addParticipantToGroupChat(roomId, authorizationHeader);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/history/{roomId)")
    public ResponseEntity<?> getChatHistory
          (@PathVariable Long roomId,
           @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(roomId,authorizationHeader);
    return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
  }

//  체팅 메시지 읽음 처리
    @PostMapping("/room/{roomId}}/read")
    public ResponseEntity<?> messageRead
    (@PathVariable Long roomId,
     @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    chatService.messageRead(roomId, authorizationHeader);
    return ResponseEntity.ok().build();
    }

//    내 채팅방 목록조회 : roomId, roomName, 그룹채팅 여부, 메시지 읽음개수
    @GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms
    (@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRoom(authorizationHeader);
      return new ResponseEntity<>(데이터, HttpStatus.OK);
    }

//    채팅방 나가기
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<?> leaveGroupRoom
    (@PathVariable Long roomId,
     @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
      chatService.leaveGroupRoom(roomId, authorizationHeader);
      return  ResponseEntity.ok().build();
    }

//    개인 채팅방 개설 또는 기존 roomId return
    @PostMapping("/room/private/create")
    public ResponseEntity<?> getOrCreatePrivateChatRoom
    (@RequestParam Long orderUserId,
     @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
      Long roomId = chatService.getOrCreatePrivateRoom(orderUserId, authorizationHeader);
      return new ResponseEntity<>(roomId, HttpStatus.OK);
    }
}
