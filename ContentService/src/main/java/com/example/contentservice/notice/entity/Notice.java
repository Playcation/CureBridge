package com.example.contentservice.notice.entity;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "`notice`")
@Builder
public class Notice extends BaseEntityDeletedAt {

  @GeneratedValue
  @Id
  private Long id;

  private String title;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  private Long userId;

  @Column(nullable = false)
  private Long viewCount;

  // 게시물 수정 시 업데이트
  public void update(String title, String content) {
    this.title = title == null ? this.title : title;
    this.content = content == null ? this.content : content;
    this.updatedAt = LocalDateTime.now(); // 수정 시간 갱신
  }

  // 조회수 증가
  public void incrementViewCount() {
    this.viewCount++;
  }


}