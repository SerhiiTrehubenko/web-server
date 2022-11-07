package com.tsa.server.domain.util;

public enum HttpStatus {

    OK("HTTP/1.1 200 OK"),
    BAD_REQUEST("HTTP/1.1 400 Bad Request"),
    NOT_FOUND("HTTP/1.1 404 Not Found"),
    INTERNAL_SERVER_ERROR("HTTP/1.1 500 Internal Server Error"),
    NOT_IMPLEMENTED("HTTP/1.1 501 Not Implemented");
    private final String description;
    public String getDescription() {
        return description;
    }
    HttpStatus(String description) {
        this.description = description;
    }
}
