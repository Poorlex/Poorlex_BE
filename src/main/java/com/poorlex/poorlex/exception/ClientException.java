package com.poorlex.poorlex.exception;

public class ClientException extends Exception {

    private final ExceptionTag tag;

    public ClientException(final ExceptionTag tag, final String message) {
        super(message);
        this.tag = tag;
    }

    public ExceptionTag tag() {
        return tag;
    }
}
