package com.example.commonmodule.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponseDto {

  private static final ObjectMapper objectmapper = new ObjectMapper();

  private String httpStatus;

  private String errorName;

  private String message;

  public ExceptionResponseDto(ExceptionType exceptionType) {
    this.httpStatus = exceptionType.getErrorName();
    this.errorName = exceptionType.getErrorName();
    this.message = exceptionType.getMessage();
  }

  public String convertToJson() throws JsonProcessingException {
    return objectmapper.writeValueAsString(this);
  }

}