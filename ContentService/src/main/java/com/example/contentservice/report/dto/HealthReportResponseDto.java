package com.example.contentservice.report.dto;

import com.example.contentservice.report.entity.HealthReport;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HealthReportResponseDto {

  private String title;

  private String reportDate;

  private String summary;

  public static HealthReportResponseDto toDto(HealthReport healthReport) {
    return new HealthReportResponseDto(healthReport.getTitle(), healthReport.getReportDate(), healthReport.getSummary());
  }
}
