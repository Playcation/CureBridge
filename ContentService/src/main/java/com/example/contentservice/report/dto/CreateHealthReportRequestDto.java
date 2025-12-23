package com.example.contentservice.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateHealthReportRequestDto {

  private Integer reportYear;

  private Integer reportMonth;

}
