package com.example.recruitment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ExceptionResponse> exceptionHandler(CustomException c) {
    return ResponseEntity.badRequest()
        .body(new ExceptionResponse(c.getMessage(), c.getErrorCode()));
  }

  @Getter
  @ToString
  @AllArgsConstructor
  public static class ExceptionResponse {

    private String message;
    private ErrorCode errorCode;
  }
}