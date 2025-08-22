package com.example.commonmodule.base_entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityDeletedAt extends BaseEntityUpdatedAt {

	protected LocalDateTime deletedAt = null;

	protected void delete(){
		this.deletedAt = LocalDateTime.now();
	}
	
}