package com.example.chat.config;

import com.example.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;


@Component
public class StompHandler implements ChannelInterceptor {

  private final ChatService chatService;

  private SecretKey secretKey;

  public StompHandler(ChatService chatService, SecretKey secretKey) {
    this.chatService = chatService;
    this.secretKey = secretKey;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // ✅ 1. STOMP CONNECT
    if (StompCommand.CONNECT == accessor.getCommand()) {
      System.out.println("connect 요청시 토큰 유호성 검증");
      System.out.println("[STOMP DEBUG] headers = " + accessor.toNativeHeaderMap());

      String bearerToken = accessor.getFirstNativeHeader("Authorization");
      String token = bearerToken.substring(7);

      Claims claims = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      System.out.println("토큰 검증 완료");

      Object raw = claims.get("userId");
      Long userId = Long.valueOf(raw.toString());

      accessor.getSessionAttributes().put("token", token);
      accessor.getSessionAttributes().put("userId", userId);
    }

    if(StompCommand.SUBSCRIBE == accessor.getCommand()) {
      System.out.println("SUBSCRIBE 검증");

      String token = (String) accessor.getSessionAttributes().get("token");

      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
      String roomId = accessor.getDestination().split("/")[2];
      if(!chatService.isRoomParticipant(Long.parseLong(roomId), token)) {
        throw new AuthenticationServiceException("해당 room에 권한이 없습니다.");
      }
    }
    return message;
  }
}
