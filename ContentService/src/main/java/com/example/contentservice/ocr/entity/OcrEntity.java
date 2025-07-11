package com.example.contentservice.ocr.entity;

import com.example.commonmodule.base_entity.BaseEntityUpdatedAt;
import lombok.Getter;

@Getter
public class OcrEntity extends BaseEntityUpdatedAt {

  private Long ocrId;

  private Long userId;

  private String reportTitle;

  private String reportDate;

  private String parsedText;

}
