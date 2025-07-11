package com.example.chat.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws-chat") // WebSocket 연결 경로
        .setAllowedOriginPatterns("*") // CORS 허용
        .withSockJS(); // SockJS fallback
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트 → 서버 메시지 전송
    registry.enableSimpleBroker("/sub"); // 서버 → 클라이언트 구독 경로
  }
}
