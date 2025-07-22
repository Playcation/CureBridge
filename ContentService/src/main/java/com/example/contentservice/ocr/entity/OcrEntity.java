package com.example.contentservice.ocr.entity;

import com.example.commonmodule.base_entity.BaseEntityUpdatedAt;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OcrEntity extends BaseEntityUpdatedAt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ocrId;

  private Long userId;

  private String reportTitle;

  private String reportDate;

  private String parsedText;

}
