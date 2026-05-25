package com.example.contentservice.report.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthReportListResponseDto {
  private List<HealthReportResponseDto> reports;
}