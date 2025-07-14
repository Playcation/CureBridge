package com.example.contentservice.board.entity;

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
@Table(name = "`board`")
@Builder
public class Board extends BaseEntity {
	@GeneratedValue
	@Id
	private Long id;

	private String title;
	private String content;

	private Long userId;
	private BoardType boardType;

}