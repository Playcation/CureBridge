package com.example.contentservice.support.entity;

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
@Table(name = "`support`")
@Builder
public class Support extends BaseEntityDeletedAt {
	@GeneratedValue
	@Id
	private Long id;

	private String title;
	private String content;
	private Long userId;

	/* (추가) 비공개/공개 여부 고민해보기 */
	/* (추가) 첨부 파일, 글 중간 사진 컬럼 추가 */

	private boolean isReplied;
	private String replyContent;
	private LocalDateTime repliedAt;

}