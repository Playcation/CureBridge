package com.example.contentservice.ocr.dto;

import com.example.contentservice.ocr.entity.OcrEntity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OcrMultiResponseDto {

  private String ocrId;

  private String reportTitle;

  private LocalDate reportDate;

  public static OcrMultiResponseDto toDto(OcrEntity ocrEntity) {
    return OcrMultiResponseDto.builder()
        .ocrId(ocrEntity.getOcrId())
        .reportTitle(ocrEntity.getReportTitle())
        .reportDate(ocrEntity.getReportDate())
        .build();
  }

}
