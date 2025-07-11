package com.example.commonmodule.base_entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityCreatedAt {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

}
