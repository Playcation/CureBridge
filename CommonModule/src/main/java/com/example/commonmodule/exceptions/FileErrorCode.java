package com.example.commonmodule.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ExceptionType {

  INVALID_FILE(HttpStatus.BAD_REQUEST, "잘못된 파일입니다."),

  FAIL_UPLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

  // NotFoundException
  NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

  // DuplicatedException
  EMAIL_FILE_NAME(HttpStatus.BAD_REQUEST, "중복된 파일 명 입니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
