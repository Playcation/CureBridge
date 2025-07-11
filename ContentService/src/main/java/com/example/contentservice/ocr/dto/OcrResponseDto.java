package com.example.contentservice.ocr.dto;

import com.example.contentservice.ocr.entity.OcrEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OcrResponseDto {

  private Long reportId;

  private String reportTitle;

  private String reportDate;

  private LocalDateTime createdAt;

  public static OcrResponseDto toDto(OcrEntity ocrEntity) {
    return OcrResponseDto.builder()
        .reportId(ocrEntity.getOcrId())
        .reportTitle(ocrEntity.getReportTitle())
        .reportDate(ocrEntity.getReportDate())
        .createdAt(ocrEntity.getCreatedAt())
        .build();
  }

}
