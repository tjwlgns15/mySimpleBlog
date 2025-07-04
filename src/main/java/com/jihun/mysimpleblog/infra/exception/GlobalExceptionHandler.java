package com.jihun.mysimpleblog.infra.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
        log.error("CustomException occurred: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException e) {
        log.error("Validation error occurred: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .code(SC_BAD_REQUEST)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .code(SC_INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
