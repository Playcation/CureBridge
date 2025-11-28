package com.example.commonmodule.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminException implements ExceptionType {

  // NOT FOUND
  NOT_FOUND_ADMIN("관리자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  // DUPLICATED
  DUPLICATED_ADMIN("이미 존재하는 관리자입니다.", HttpStatus.BAD_REQUEST),
  // NO_AUTHORIZED
  NO_AUTHORIZED_ADMIN("관리자 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
  // INVALID_INPUT
  INVALID_INPUT_ADMIN("유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST);
  // INTERNAL_SERVER

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
