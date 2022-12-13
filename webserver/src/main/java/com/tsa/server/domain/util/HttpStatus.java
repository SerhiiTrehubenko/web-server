package com.tsa.server.domain.util;

public enum HttpStatus {

    OK("200 OK"),
    BAD_REQUEST("400 Bad Request"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    NOT_IMPLEMENTED("501 Not Implemented"),

    LOST_CONNECTION("Connection with a client was lost"),
    HTTP_SUFFIX("HTTP/1.1 ");
    private final String status;

    public String getStatus() {
        return status;
    }

    HttpStatus(String status) {
        this.status = status;
    }
}
