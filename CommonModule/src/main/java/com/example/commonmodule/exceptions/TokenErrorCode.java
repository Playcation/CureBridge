package com.example.commonmodule.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ExceptionType {

  TOKEN_CATEGORY_MISS_MATCH(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다: 카테고리 불일치"),

  REFRESH_TOKEN_MISS_MATCH(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다: 리플레시 토큰 불일치"),

  // NoAuthorizedException
  NO_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "다시 로그인해주시기 바랍니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
