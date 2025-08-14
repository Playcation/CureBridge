package com.example.commonmodule.files.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`noticefile`")
public class BoardFile {

  @Id
  @GeneratedValue
  private Long id;

  private Long boardId;
  private Long fileDetailId;

  @Enumerated(EnumType.STRING)
  private BoardFileType fileType;

}