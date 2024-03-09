package com.poorlex.poorlex.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handle(final IllegalArgumentException exception) {
        exception.printStackTrace();
        final ExceptionResponse exceptionResponse = new ExceptionResponse(exception);
        log.warn("Handled IllegalArgumentException : {}", exceptionResponse);
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handle(final Exception exception) {
        exception.printStackTrace();
        final ExceptionResponse exceptionResponse = new ExceptionResponse(exception);
        log.warn("Handled IllegalArgumentException : {}", exceptionResponse);
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }
}
