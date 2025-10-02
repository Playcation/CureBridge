package com.example.chat.controller;

import com.example.chat.dto.ChatRoomListResponseDto;
import com.example.chat.service.ChatService;
import com.example.memberservice.security.TokenSettings;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
