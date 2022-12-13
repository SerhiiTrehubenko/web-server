package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Response;
import com.tsa.server.domain.exceptions.WebServerException;
import com.tsa.server.domain.exceptions.WriteToSocketException;

import java.io.*;

public class ResponseWriter {
    public static void writeSuccessResponse(InputStream inputFromFile,
                                            OutputStream outputToSocket,
                                            Response response) throws WriteToSocketException, WebServerException {
        try (inputFromFile) {
            var outToSocket = new BufferedOutputStream(outputToSocket);

            try {
                writeResponse(outToSocket, response);
                writeResponseBody(inputFromFile, outToSocket);
                outToSocket.flush();
            } catch (IOException e) {
                try {
                    outputToSocket.flush();
                } catch (IOException ex) {
                    throw new WriteToSocketException(e);
                }
                throw new WebServerException(e, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeErrorResponse(OutputStream outputToSocket, Response response) {
        try (var outToSocket = new BufferedOutputStream(outputToSocket)) {
            outToSocket.write(responseToBytes(response));
        } catch (IOException e) {
            throw new WebServerException(e, HttpStatus.LOST_CONNECTION);
        }
    }

    static void writeResponse(OutputStream outToSocket, Response response) throws WriteToSocketException {
        try {
            outToSocket.write(responseToBytes(response));
        } catch (IOException e) {
            throw new WriteToSocketException(e);
        }
    }

    static void writeResponseBody(InputStream inputFromFile, OutputStream outToSocket) throws WriteToSocketException, IOException {
        byte[] buffer = new byte[8 * 1024];
        int count;
        while ((count = inputFromFile.read(buffer)) != -1) {
            try {
                outToSocket.write(buffer, 0, count);
            } catch (IOException e) {
                throw new WriteToSocketException(e);
            }
        }
    }

    static byte[] responseToBytes(Response response) {
        return (HttpStatus.HTTP_SUFFIX.getStatus() + response.getHttpStatus().getStatus() +
                response.getContent() + response.getExceptionMessage()).getBytes();
    }
}
