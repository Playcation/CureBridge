package com.example.contentservice.report.dto;

import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.report.entity.HealthReport;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthReportResponseDto implements Serializable {

  private String id;

  private String title;

  private Integer count;

  private LocalDate reportDate;

  private List<OcrResponseDto> ocrResults;

  private String summery;

  private Integer rate;

  public static HealthReportResponseDto toDto(HealthReport healthReport,
      List<OcrResponseDto> ocrResults) {
    return HealthReportResponseDto.builder()
        .id(healthReport.getId())
        .count(ocrResults.size())
        .title(healthReport.getTitle())
        .ocrResults(ocrResults)
        .summery(healthReport.getSummary())
        .rate(healthReport.getRate())
        .reportDate(healthReport.getReportDate())
        .build();
  }
}
