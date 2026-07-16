package com.example.contentservice.ocr.dto;

import com.example.contentservice.ocr.entity.OcrEntity;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResponseDto implements Serializable {

  private String reportId;

  private String reportTitle;

//  private String reportContent;

  private String patientName;

  private String diagnosis;

  private LocalDate reportDate;

  private LocalDateTime createdAt;

  public static OcrResponseDto toDto(OcrEntity ocrEntity) {
    return OcrResponseDto.builder()
        .reportId(ocrEntity.getOcrId())
        .reportTitle(ocrEntity.getReportTitle())
//        .reportContent(ocrEntity.getUpdatedText())
        .patientName(ocrEntity.getPatientName())
        .diagnosis(ocrEntity.getDiagnosis())
        .reportDate(ocrEntity.getReportDate())
        .createdAt(ocrEntity.getCreatedAt())
        .build();
  }

}
