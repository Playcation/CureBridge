package com.example.chat.domain;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;
import com.example.memberservice.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReadStatus extends BaseEntityDeletedAt {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id" ,nullable = false)
  private ChatRoom chatRoom;

  @JoinColumn(name = "user_id" ,nullable = false)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_message_id", nullable = false)
  private ChatMessage chatMessage;

  @Column(nullable = false)
  private Boolean isRead;

}
