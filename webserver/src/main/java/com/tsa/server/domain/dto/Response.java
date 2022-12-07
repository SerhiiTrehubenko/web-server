package com.tsa.server.domain.dto;


import com.tsa.server.domain.util.HttpStatus;

public class Response {
    private final HttpStatus httpStatus;
    private final String content = "\r\n\r\n";

    public Response(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContent() {
        return content;
    }
}
