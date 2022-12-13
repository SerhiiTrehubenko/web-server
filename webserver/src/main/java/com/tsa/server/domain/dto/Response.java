package com.tsa.server.domain.dto;


import com.tsa.server.domain.util.HttpStatus;

public class Response {
    private static final String MESSAGE_BODY = "\r\n\r\n";
    private final HttpStatus httpStatus;
    private final String exceptionMessage;

    public Response(HttpStatus httpStatus) {
        this(httpStatus, "");
    }

    public Response(HttpStatus httpStatus, String exceptionMessage) {
        this.httpStatus = httpStatus;
        this.exceptionMessage = exceptionMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContent() {
        return MESSAGE_BODY;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
