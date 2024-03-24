package com.poorlex.poorlex.exception;

public class ApiException extends RuntimeException {

    private final ExceptionTag tag;

    public ApiException(final ExceptionTag tag, final String message) {
        super(message);
        this.tag = tag;
    }

    public ExceptionTag getTag() {
        return tag;
    }
}
