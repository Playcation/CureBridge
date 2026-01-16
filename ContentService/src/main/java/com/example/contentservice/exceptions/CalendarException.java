package com.example.contentservice.exceptions;

import com.example.commonmodule.exceptions.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarException implements ExceptionType {

  NOT_FOUND_CALENDAR(HttpStatus.NOT_FOUND, "일정을 찾지 못했습니다."),
  NO_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }

}
