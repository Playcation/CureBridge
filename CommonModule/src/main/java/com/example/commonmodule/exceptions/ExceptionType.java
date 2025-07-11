package com.example.commonmodule.exceptions;

import org.springframework.http.HttpStatus;

public interface ExceptionType {

  HttpStatus getHttpStatus();

  String getErrorName();

  String getMessage();

}
