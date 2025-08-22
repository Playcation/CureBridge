package com.example.contentservice.report.dto;

import com.example.contentservice.report.entity.HealthReport;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HealthReportMultiResponseDto {

  private String id;

  private String title;

  private String reportDate;

  public static HealthReportMultiResponseDto toDto(HealthReport healthReport) {
    return new HealthReportMultiResponseDto(healthReport.getId(), healthReport.getTitle(), healthReport.getReportDate());
  }
}
