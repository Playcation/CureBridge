package com.example.contentservice.exceptions;

import com.example.commonmodule.exceptions.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HealthReportException implements ExceptionType {

  NOT_FOUND_HEALTH_REPORT(HttpStatus.NOT_FOUND, "헬스레포트를 찾지 못했습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }

}
