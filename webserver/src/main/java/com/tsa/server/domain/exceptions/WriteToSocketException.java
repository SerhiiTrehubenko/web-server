package com.tsa.server.domain.exceptions;

public class WriteToSocketException extends Exception {
    public WriteToSocketException(Throwable cause) {
        super(cause);
    }

    public WriteToSocketException() {
    }
}
