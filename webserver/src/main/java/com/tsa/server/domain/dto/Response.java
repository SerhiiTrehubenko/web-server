package com.tsa.server.domain.dto;


import com.tsa.server.domain.util.HttpStatus;

public class Response {
    private final HttpStatus httpStatus;
    private static final String CONTENT = "\r\n\r\n";

    public Response(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return httpStatus.getDescription() + CONTENT;
    }
}
