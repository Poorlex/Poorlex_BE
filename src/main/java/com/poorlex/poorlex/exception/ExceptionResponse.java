package com.poorlex.poorlex.exception;

public class ExceptionResponse {

    private final String tag;
    private final String message;

    public ExceptionResponse(final ExceptionTag tag, final String message) {
        this.tag = tag.getValue();
        this.message = message;
    }

    public static ExceptionResponse from(final ApiException apiException) {
        return new ExceptionResponse(apiException.getTag(), apiException.getMessage());
    }

    public static ExceptionResponse from(final Exception exception) {
        return new ExceptionResponse(null, exception.getMessage());
    }

    public String getMessage() {
        return message;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "ExceptionResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
