package com.tsa.server.domain.services;

import static com.tsa.server.domain.util.RequestParser.*;
import static com.tsa.server.domain.util.ResponseWriter.*;

import com.tsa.server.domain.dto.Request;
import com.tsa.server.domain.dto.Response;
import com.tsa.server.domain.exceptions.*;
import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.interfaces.RequestHandler;
import com.tsa.server.domain.util.HttpMethod;
import com.tsa.server.domain.util.HttpStatus;

import java.io.InputStream;
import java.io.OutputStream;

public class DefaultRequestHandler implements RequestHandler {

    private final InputStream inputFromSocket;
    private final OutputStream outputToSocket;
    private final ContentReader contentReader;

    /**
     * for testing purpose
     */
    DefaultRequestHandler() {
        this(null, null, null);
    }

    public DefaultRequestHandler(InputStream inputFromSocket,
                                 OutputStream outputToSocket,
                                 ContentReader contentReader) {
        this.inputFromSocket = inputFromSocket;
        this.outputToSocket = outputToSocket;
        this.contentReader = contentReader;
    }

    @Override
    public void handle() {
        try {
            Request parsedRequest = parseRequest(inputFromSocket);

            checkHttpMethodOnImplementation(parsedRequest.getHttpMethod());

            var inputFromSourceFile = contentReader.getConnectionToContent(parsedRequest.getUri());
            writeSuccessResponse(inputFromSourceFile, outputToSocket, new Response(HttpStatus.OK));

        } catch (WriteToSocketException e) {
            throw new RuntimeException(e);
        } catch (WebServerException e) {
            writeErrorResponse(outputToSocket, new Response(e.getStatus()));
            throw new RuntimeException(e);
        }
    }

    void checkHttpMethodOnImplementation(HttpMethod httpMethod) throws WebServerException {
        if (!httpMethod.isImplemented()) {
            throw new WebServerException("HTTP method is not implemented", HttpStatus.NOT_IMPLEMENTED);
        }
    }
}
