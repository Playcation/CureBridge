package com.example.contentservice.support.entity;

import java.time.LocalDateTime;

import com.example.commonmodule.base_entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

	public void update(String title, String content) {
		this.title = title == null ? this.title : title;
		this.content = content == null ? this.content : content;
		this.updatedAt = LocalDateTime.now(); // 수정 시간 갱신
		this.isPrivate = false;
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}