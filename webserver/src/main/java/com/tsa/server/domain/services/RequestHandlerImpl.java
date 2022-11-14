package com.tsa.server.domain.services;

import com.tsa.server.domain.dto.Request;
import com.tsa.server.domain.dto.Response;
import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.interfaces.RequestHandler;
import com.tsa.server.domain.util.HttpStatus;
import com.tsa.server.domain.util.RequestParser;
import com.tsa.server.domain.util.ResponseWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestHandlerImpl implements RequestHandler {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ContentReader contentReader;

    public RequestHandlerImpl(InputStream inputStream,
                              OutputStream outputStream,
                              ContentReader contentReader) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.contentReader = contentReader;
    }

    @Override
    public void handle() {
        Request parsedRequest;
        try {
            parsedRequest = RequestParser.parseRequest(inputStream);
        } catch (Exception e) {
            ResponseWriter.writeErrorResponse(outputStream, new Response(HttpStatus.BAD_REQUEST));
            throw new RuntimeException(e);
        }

        if (!parsedRequest.getHttpMethod().getDescription()) {

            ResponseWriter.writeErrorResponse(outputStream, new Response(HttpStatus.NOT_IMPLEMENTED));
            throw new RuntimeException("HTTP method is not implemented");
        }
        try (var input = contentReader.readContent(parsedRequest.getUri())) {

            ResponseWriter.writeResponse(input, outputStream, new Response(HttpStatus.OK));
        } catch (FileNotFoundException e) {
            ResponseWriter.writeErrorResponse(outputStream, new Response(HttpStatus.NOT_FOUND));
            throw new RuntimeException("FILE is not found");
        } catch (IOException e) {
            ResponseWriter.writeErrorResponse(outputStream, new Response(HttpStatus.INTERNAL_SERVER_ERROR));
            throw new RuntimeException("Internal server error");
        }

    }

}
