package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ResponseWriter {
    public static void writeSuccessResponse(InputStream inputFromFile,
                                            OutputStream outputToSocket,
                                            Response response) {

        byte[] buffer = new byte[8 * 1024];
        int count;
        try (var inputFile = new DataInputStream(inputFromFile)) {

            var outToSocket = new DataOutputStream(outputToSocket);
            outToSocket.write(responseToBytes(response));

            while ((count = inputFile.read(buffer)) != -1) {
                outToSocket.write(buffer, 0, count);
                outToSocket.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeErrorResponse(OutputStream outputToSocket, Response response) {
        try {
            var out = new DataOutputStream(outputToSocket);
            out.write(responseToBytes(response));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] responseToBytes(Response response) {
        return (response.getHttpStatus().getDescription() + response.getContent()).getBytes();
    }
}
