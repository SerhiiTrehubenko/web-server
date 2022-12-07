package com.tsa.webserver;

public enum HttpStatus {
    OK ("HTTP/1.1 200 OK"),
    NOT_FOUND ("HTTP/1.1 404 Not Found"),
    SEVER_ERROR ("HTTP/1.1 500 Internal Server Error");
    private final String httpStatus;

    public String getStatus() {
        return httpStatus;
    }
    HttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }
}
