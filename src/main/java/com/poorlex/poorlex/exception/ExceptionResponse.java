package com.poorlex.poorlex.exception;

public class ExceptionResponse {

    private final String tag;
    private final String message;

    public ExceptionResponse(final String tag, final String message) {
        this.tag = tag;
        this.message = message;
    }

    public static ExceptionResponse from(final ApiException apiException) {
        return new ExceptionResponse(apiException.getTag().getValue(), apiException.getMessage());
    }

    public static ExceptionResponse from(final ClientErrorException clientErrorException) {
        return new ExceptionResponse(clientErrorException.getTag().getValue(), clientErrorException.getMessage());
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
