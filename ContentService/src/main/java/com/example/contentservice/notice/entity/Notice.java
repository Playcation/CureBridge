package com.example.contentservice.notice.entity;

import java.time.LocalDateTime;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;

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
@Table(name = "`board`")
@Builder
public class Notice extends BaseEntityDeletedAt {
	@GeneratedValue
	@Id
	private Long id;

	private String title;
	private String content;

	private Long userId;

	/* (추가) 첨부 파일, 글 중간 사진 컬럼 추가 */

	// 게시물 수정 시 업데이트
	public void update(String title, String content) {
		this.title = title == null ? this.title : title;
		this.content = content == null ? this.content : content;
		this.updatedAt = LocalDateTime.now(); // 수정 시간 갱신
	}

}