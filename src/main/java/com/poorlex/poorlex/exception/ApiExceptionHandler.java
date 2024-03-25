package com.poorlex.poorlex.exception;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    private static final String HANDLED_EXCEPTION_LOG_MESSAGE = "[Exception] Tag : {} / Message : {}";
    private static final String UNHANDLED_EXCEPTION_LOG_MESSAGE = "Unhandled Exception : {}";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handle(final ApiException exception) {
        setSentryTag(exception);
        captureWithSentry(exception);
        log.warn(HANDLED_EXCEPTION_LOG_MESSAGE, exception.getTag().getValue(), exception.getMessage());
        return ResponseEntity.badRequest().body(ExceptionResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handle(final Exception exception) {
        captureWithSentry(exception);
        exception.printStackTrace();
        log.warn(UNHANDLED_EXCEPTION_LOG_MESSAGE, exception.getClass());
        return ResponseEntity.internalServerError().body(ExceptionResponse.from(exception));
    }

    private void setSentryTag(final ApiException exception) {
        Sentry.setTag("tag", exception.getTag().getValue());
    }

    private void captureWithSentry(final Exception exception) {
        Sentry.captureException(exception);
    }
}
