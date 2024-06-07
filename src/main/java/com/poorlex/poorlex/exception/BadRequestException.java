package com.poorlex.poorlex.exception;

public class BadRequestException extends ClientErrorException {

    public BadRequestException(final ExceptionTag tag, final String message) {
        super(tag, message);
    }
}
