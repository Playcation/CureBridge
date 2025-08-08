package com.example.commonmodule.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionType {

	EMPTY_INPUT_FIELDS(HttpStatus.BAD_REQUEST, "필수 입력 항목 중 공백인 항목이 있습니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "이전 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),
	NO_AUTHORIZED_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String getErrorName() {
		return this.name();
	}
}
