package com.poorlex.poorlex.exception;

public class BadRequestException extends ClientException {

    private final ExceptionTag tag;

    public BadRequestException(final ExceptionTag tag, final String message) {
        super(message);
        this.tag = tag;
    }

    public ExceptionTag getTag() {
        return tag;
    }
}
