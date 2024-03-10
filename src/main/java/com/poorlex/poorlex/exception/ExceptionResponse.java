package com.poorlex.poorlex.exception;

public class ExceptionResponse {
    private final String message;

    public ExceptionResponse(final Exception exception) {
        this.message = exception.getMessage();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ExceptionResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
