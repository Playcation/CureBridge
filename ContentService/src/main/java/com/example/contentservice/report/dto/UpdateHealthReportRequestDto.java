package com.example.contentservice.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHealthReportRequestDto {

  private String title;

  private String reportDate;

  private String summary;

}
