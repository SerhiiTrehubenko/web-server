package com.tsa.server.domain.dto;

import com.tsa.server.domain.util.HttpMethod;

import java.util.Map;

public class Request {
    private HttpMethod httpMethod;
    private String uri;
    private Map<String, String> headers;

    public Request() {
        this(null, null, null);
    }

    public Request(String httpMethod, String uri, Map<String, String> headers) {
        this.httpMethod = httpMethod == null ? null : HttpMethod.valueOf(httpMethod);
        this.uri = uri;
        this.headers = headers;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod == null ? null : HttpMethod.valueOf(httpMethod);
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Request{" +
                "httpMethod=" + httpMethod +
                ", uri='" + uri + '\'' +
                ", headers=" + headers +
                '}';
    }
}
