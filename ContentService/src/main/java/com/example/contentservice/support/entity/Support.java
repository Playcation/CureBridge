package com.example.contentservice.support.entity;

import com.example.commonmodule.base_entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`support`")
@Builder
public class Support extends BaseEntity {

  @GeneratedValue
  @Id
  private Long id;

  private String title;
  private String content;
  private Long userId;

  private boolean isPrivate;

  // 답글 부분 컬럼
  private boolean isReplied;
  private String replyContent;
  private LocalDateTime repliedAt;  // 굳이 수정 시간 필요할까 싶어서 고민 중..

  // 문의 수정 시 업데이트
  public void update(String title, String content, boolean isPrivate) {
    if (title != null) {
      this.title = title;
    }
    if (content != null) {
      this.content = content;
    }
    this.isPrivate = isPrivate;
  }

  // 답글 수정 시 업데이트
  public void updateReply(String replyContent) {
    this.replyContent = replyContent == null ? this.replyContent : replyContent;
    this.repliedAt = LocalDateTime.now(); // 수정 시간 갱신
  }

  public void deleteReply() {
    this.replyContent = null;
    this.isReplied = false;
    this.repliedAt = null;
  }


}