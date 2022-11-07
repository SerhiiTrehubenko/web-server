package com.tsa.server.domain.util;

public enum HttpMethod {
    GET(true),
    POST(false),
    DELETE(false),
    PUT(false),
    PATCH(false);

    private final boolean description;
    public boolean getDescription() {
        return description;
    }
    HttpMethod(boolean description) {
        this.description = description;
    }
}
