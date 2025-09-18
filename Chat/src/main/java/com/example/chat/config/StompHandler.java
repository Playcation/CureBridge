package com.example.chat.config;

import com.example.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.naming.AuthenticationException;

@Component
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;

  @Value("${jwt.secretKey}")
  private String secretKey;

    public StompHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // 토큰 유효성 검사로 회원가입을 완료한 유저만 사용할수 있도록 변경(추후 민님 코드보면서 수정후 테스트 필요)
    if(StompCommand.CONNECT == accessor.getCommand()) {
      System.out.println("connect요청시 토큰 유효성 검증");
      String bearerToken = accessor.getFirstNativeHeader("Authorization");
      String token = bearerToken.substring(7);

      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();
      System.out.println("토큰 검증 완료");
    }
    if(StompCommand.SUBSCRIBE == accessor.getCommand()) {
        System.out.println("subscribe 검증");
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        String token = bearerToken.substring(7);
        Claims claims =  Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String email = claims.getSubject();
        String roomId = accessor.getDestination().split("/")[2];
        if(!chatService.isRoomParticipant(email, Long.parseLong(roomId),bearerToken)) {
            throw new AuthenticationServiceException("해당 room에 권한이 없습니다");
        }
    }

    return message;
  }
}
