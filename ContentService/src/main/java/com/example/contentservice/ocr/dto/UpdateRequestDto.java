package com.example.contentservice.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateRequestDto {

  private String reportTitle;

  private String reportDate;

  private String patientName;

  private String diagnosis;

  private List<String> rawText;

}
