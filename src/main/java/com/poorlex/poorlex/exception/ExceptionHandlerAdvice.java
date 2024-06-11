package com.poorlex.poorlex.exception;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    private static final String HANDLED_EXCEPTION_LOG_MESSAGE = "[Exception] Tag : {} / Message : {}";
    private static final String UNHANDLED_EXCEPTION_LOG_MESSAGE = "Unhandled Exception : {}";

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MissingRequestValueException.class})
    public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NoResourceFoundException ex) throws NoResourceFoundException {
        throw ex;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) throws HttpRequestMethodNotSupportedException {
        throw ex;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException ex) throws AuthenticationException {
        throw ex;
    }


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handle(final ApiException exception) {
        setSentryTag(exception);
        captureWithSentry(exception);
        log.error(HANDLED_EXCEPTION_LOG_MESSAGE, exception.getTag().getValue(), exception.getMessage());
        return ResponseEntity.badRequest().body(ExceptionResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handle(final Exception exception) {
        captureWithSentry(exception);
        exception.printStackTrace();
        log.error(UNHANDLED_EXCEPTION_LOG_MESSAGE, exception.getClass());
        return ResponseEntity.internalServerError().body(ExceptionResponse.from(exception));
    }

    private void setSentryTag(final ApiException exception) {
        Sentry.setTag("tag", exception.getTag().getValue());
    }

    private void captureWithSentry(final Exception exception) {
        Sentry.captureException(exception);
    }
}