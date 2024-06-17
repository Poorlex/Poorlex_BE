package com.poorlex.poorlex.exception;

public class ForbiddenException extends ClientErrorException {

    public ForbiddenException(ExceptionTag tag, String message) {
        super(tag, message);
    }
}
