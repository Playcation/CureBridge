package com.example.chat.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


//connect로 웹소켓 연결요청이 들어왔을때 이를 처리할 클래스
@Slf4j
@Component
public class SimpleWebsocketHandler extends TextWebSocketHandler {

  // 연결된 세션 관리 : 스레드 safe한 set 사용
  private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
  Logger logger = LoggerFactory.getLogger(SimpleWebsocketHandler.class);

  // 사용자의 정보를 서버의 메모리에 저장해주는 메서드
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessions.add(session);
    logger.info("connected : " + session.getId());
  }

  // 사용자에게 메시지를 보내주는 메서드
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    logger.info("received message : " + payload);
    for(WebSocketSession s : sessions) {
      if (s.isOpen()) {
        s.sendMessage(new TextMessage(payload));
      }
    }
  }

  // 사용자의 정보를 서버의 메모리에서 제외시켜주는 메서드
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    sessions.remove(session);
    logger.info("disconnected!!");
  }

}
