package com.example.commonmodule.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ExceptionType {
  EXIST_REPLY("이미 답글이 작성된 문의입니다.", HttpStatus.BAD_REQUEST),
  NOT_FOUND_REPLY("답글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_BOARD("게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  REPLIED_SUPPORT("이미 답변된 게시물은 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_OWNER("작성자가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
  DELETED_BOARD("삭제된 게시물입니다.", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
