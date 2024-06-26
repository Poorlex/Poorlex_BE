package com.poorlex.poorlex.exception;

public class ConflictException extends ClientErrorException {

    public ConflictException(ExceptionTag tag, String message) {
        super(tag, message);
    }
}
