package com.example.contentservice.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateRequestDto {

  private String reportTitle;

  private String reportDate;

  private String updatedText;

}
