package com.example.commonmodule.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ExceptionType {
	NOT_FOUND_BOARD("게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	DELETED_BOARD("삭제된 게시물입니다.", HttpStatus.BAD_REQUEST);

	private final String message;
	private final HttpStatus httpStatus;

	@Override
	public String getErrorName() {
		return this.name();
	}
}
