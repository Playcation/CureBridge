package com.example.chat.domain;


import com.example.commonmodule.base_entity.BaseEntityDeletedAt;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field.Str;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoom extends BaseEntityDeletedAt {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Builder.Default
  private String isGroupChat = "N";

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
  private List<ChatParticipant> chatParticipants = new ArrayList<>();

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<ChatMessage> chatMessages = new ArrayList<>();

}
