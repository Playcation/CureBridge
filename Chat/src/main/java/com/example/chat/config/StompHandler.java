package com.example.chat.config;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

  @Value("${jwt.secretKey}")
  private String secretKey;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // 토큰 유효성 검사로 회원가입을 완료한 유저만 사용할수 있도록 변경(추후 민님 코드보면서 수정후 테스트 필요)
//    if(StompCommand.CONNECT == accessor.getCommand()) {
//      System.out.println("connect요청시 토큰 유효성 검증");
//      String bearerToken = accessor.getFirstNativeHeader("Authorization");
//      String token = bearerToken.substring(7);
//
//      Jwts.parserBuilder()
//          .setSigningKey(secretKey)
//          .build()
//          .parseClaimsJws(token)
//          .getBody();
//      System.out.println("토큰 검증 완료");
//    }

    return message;
  }
}
