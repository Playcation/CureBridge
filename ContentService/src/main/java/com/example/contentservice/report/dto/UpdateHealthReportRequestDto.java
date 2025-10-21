package com.example.contentservice.report.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHealthReportRequestDto {

  private String title;

  private LocalDate reportDate;

  private String summary;

  private Integer rate;

}
