package com.poorlex.poorlex.exception;

public class ClientErrorException extends RuntimeException {

    private final ExceptionTag tag;

    public ClientErrorException(final ExceptionTag tag, final String message) {
        super(message);
        this.tag = tag;
    }

    public ExceptionTag getTag() {
        return tag;
    }
}
