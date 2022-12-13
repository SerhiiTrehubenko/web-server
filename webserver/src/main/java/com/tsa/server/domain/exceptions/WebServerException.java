package com.tsa.server.domain.exceptions;

import com.tsa.server.domain.util.HttpStatus;

public class WebServerException extends RuntimeException {

    private final HttpStatus status;

    public WebServerException(HttpStatus status) {
        this.status = status;
    }

    public WebServerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public WebServerException(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
