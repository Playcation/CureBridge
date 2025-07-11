package com.example.commonmodule.base_entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityDeletedAt extends BaseEntityUpdatedAt{

  private LocalDateTime deletedAt = null;

  public void delete(){
    deletedAt = LocalDateTime.now();
  }

}
