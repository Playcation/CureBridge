package com.example.commonmodule.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerException extends RuntimeException {
  private final HttpStatus httpStatus;
  private final String errorName;
  private final String message;

  public InternalServerException(ExceptionType exceptionType) {
    this.httpStatus = exceptionType.getHttpStatus();
    this.errorName = exceptionType.getErrorName();
    this.message = exceptionType.getMessage();
  }
}
